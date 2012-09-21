package com.ticketfly.nio

import com.ticketfly._
import java.net.{InetAddress,InetSocketAddress}
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import scala.util.continuations._

class NioListener(selector:NioSelector, port:Int, workers:Seq[Worker]) {

    val serverChannel = ServerSocketChannel.open()
    serverChannel.configureBlocking(false);
    val isa = new InetSocketAddress(port)
    serverChannel.socket.bind(isa)
    def start(continueListening: =>Boolean):Unit = {
        reset {
            while (continueListening) {
                val socket = accept()
                NioConnection.newConnection(selector,socket,workers)
            }
        }
    }

    private def accept():SocketChannel @suspendable = {
        shift { k: (SocketChannel => Unit) =>
            selector.register(serverChannel,SelectionKey.OP_ACCEPT, {
                val conn = serverChannel.accept()
                conn.configureBlocking(false)
                k(conn)
            })
        }
    }
}
