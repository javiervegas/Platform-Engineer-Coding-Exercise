package com.ticketfly

import nio._
import scala.actors.Actor 
import java.nio.ByteBuffer
import java.nio.charset.Charset

//Workers receive connection input, call the counter and the TFlyService to get output to send back.
//
class Worker(counter:Counter) extends Actor {
  var service = new TFlyService
  val validFormat = """([a-z_]+)( ?)(\d*)(\r\n)""".r
  def act = loop {
    react {
      case b:ByteBuffer => 
        reply(Charset.forName("UTF-8").decode(b).toString match {
           //only words and underscores
           case validFormat(word, "", "", sep) => 
             encodedResponse(word, counter !? None match { case i:Int => i}, sep)
           //words and underscores followed by space and digits
           case validFormat(word, " ", count, sep) if count != "" => 
             encodedResponse(word, counter !? count.toInt match { case i:Int => i}, sep)
           //anything else is invalid input, and will cause the connection to be closed
           case _ => new ServerInputException
        })
    }
  }

  def encodedResponse(word:String, count:Int, sep:String) = Charset.forName("UTF-8").encode(safeServiceExecute(word)+" "+count.toString+sep)

  //Workers handle service failure by creating a new service instance to replace the failed one.
  def safeServiceExecute(word:String):String = {
    try {
      service.execute(word)
    } catch {
      case ex: TFlyService.TFlyServiceException => 
        service = new TFlyService  
        safeServiceExecute(word)
    }
  }
}

class ServerInputException() extends RuntimeException("Invalid server input")

//Counter keeps track of requests, count can be overwriten by bigger numbers in input forwarded by workers.
class Counter() extends Actor {
  private var count = 1
  def act = loop {
    react {
      case int:Int if int > count =>
        reply(int+1)
        count = int+2
      case _  => 
        reply(count)
        count = count + 1
    }
  }
}

//server uses Nio to handle multiple simultaneous connections
class NioServer(val port:Int, val workerPoolSize:Int) {
    val counter = new Counter
    val workers = (1 to workerPoolSize).map{_ => new Worker(counter)}
    val selector = new NioSelector
    val listener = new NioListener(selector, port, workers)
    println("Starting server at port "+port)
    def run {
        counter.start
        workers.foreach{_.start}
        listener.start(true)
        selector.run
    }
}

object TflySimpleServer extends App {
  val port = 4567
  val workerPoolSize = 20
  val server = new NioServer(port, workerPoolSize)
  server.run
}
