package com.ticketfly.nio

import com.ticketfly._
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import scala.util.Random
import scala.util.continuations._

object NioConnection {
    def newConnection(selector:NioSelector, socket:SocketChannel, workers:Seq[Worker]) {
        val conn = new NioConnection(selector,socket,workers)
        conn.start()
    }
}

class NioConnection(selector:NioSelector, socket:SocketChannel, workers:Seq[Worker]) {

    private val buffer = ByteBuffer.allocateDirect(2000)
    private val rand = new Random

    def start():Unit = {
        reset {
            while (socket.isOpen)
                readWait
        }
    }

    private def readWait = {
        buffer.clear()
        val count = read(buffer)
        if (count<1)
            socket.close()
        else
            readAction(buffer)
    }

    private def read(b:ByteBuffer):Int @suspendable = {
        if (!socket.isOpen)
            -1  //indicate EOF
        else shift { k: (Int => Unit) =>
            selector.register(socket, SelectionKey.OP_READ, {
                val n = socket.read(b)
                k(n)
            })
        }
    }

    private def readAction(b:ByteBuffer) {
        b.flip()
        //send input to a random worker in the pool
        workers(rand.nextInt(workers.size)) !? b match {
          //input was valid, send response and keep connection open 
          case b: ByteBuffer => socket.write(b)
          //input was invalid, close connection immediately
          case ex: Exception => socket.close
        }
        b.clear()
    }
}
