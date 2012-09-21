package com.ticketfly

import nio._
import java.nio.ByteBuffer
import java.nio.charset.Charset
import org.specs2.execute.Success
import org.specs2.mutable._
import org.specs2.specification._

class TflySimpleServerSpec extends Specification {

  def counter = {
    val counter = new Counter
    counter.start
    counter
  }

  def worker = {
    val worker = new Worker(counter)
    worker.start
    worker
  }

  "The counter" should {
    "increase the count on first request" in {
       counter !? None should be equalTo 1 
    }
    "increase the count on second request" in  {
       val thecounter = counter
       thecounter !? None
       thecounter !? None should be equalTo 2 
    }
    "overwrite the counter when sent a bigger number" in  {
       val thecounter = counter
       thecounter !? None
       thecounter !? 123 should be equalTo 124 
       thecounter !? None should be equalTo 125
    }
    "be able overwrite the counter more than once" in  {
       val thecounter = counter
       thecounter !? None
       thecounter !? 123
       thecounter !? 223 should be equalTo 224 
    }
    "overwrite the counter when sent a bigger number" in  {
       val thecounter = counter
       (1 to 10).foreach(_ => thecounter !? None)
       thecounter !? 1 should be equalTo 11
    }
  }

  "The worker" should {
    "take lines with a single word" in {
      worker !? Charset.forName("UTF-8").encode("fly\r\n") should be equalTo Charset.forName("UTF-8").encode("ylf 1\r\n")
    }
    "take lines with two words separated by underscores" in {
      worker !? Charset.forName("UTF-8").encode("ticket_fly\r\n") should be equalTo Charset.forName("UTF-8").encode("ylf_tekcit 1\r\n")
    }
    "take lines with three words separated by underscores" in {
      worker !? Charset.forName("UTF-8").encode("fly_ticket_fly\r\n") should be equalTo Charset.forName("UTF-8").encode("ylf_tekcit_ylf 1\r\n")
    }
    "take lines with a single word and final digits separated by one space and return over the digits" in {
      worker !? Charset.forName("UTF-8").encode("fly 24\r\n") should be equalTo Charset.forName("UTF-8").encode("ylf 25\r\n")
    }
    "take lines words separated by underscores and final digits separated by one space and return over the digits" in {
      worker !? Charset.forName("UTF-8").encode("ticket_fly 41\r\n") should be equalTo Charset.forName("UTF-8").encode("ylf_tekcit 42\r\n")
    }
    "not take lines with words separated by space" in {
      worker !? Charset.forName("UTF-8").encode("ticket fly\r\n") match {
        case ex:ServerInputException => true should be equalTo true 
        case _ => true should be equalTo false
      }
    }
    "not take lines with a single word and final space" in {
      worker !? Charset.forName("UTF-8").encode("ticket \r\n") match {
        case ex:ServerInputException => true should be equalTo true 
        case _ => true should be equalTo false
      }
    }
    "be able to handle service failures as if they didnt happen" in {
      //there should be about 25 failures in a run of 500 calls to the FlyService
      val theworker = worker
      (1 to 500).foreach(_ => theworker !? Charset.forName("UTF-8").encode("fly\r\n"))
      theworker !? Charset.forName("UTF-8").encode("fly\r\n") should be equalTo Charset.forName("UTF-8").encode("ylf 501\r\n")
    }
  }


}
