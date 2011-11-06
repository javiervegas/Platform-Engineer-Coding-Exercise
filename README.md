Ticketfly Platform Engineer Coding Exercise
===========================================

## Simple Server

Write a simple server in a JVM language of your choosing that will bind to a port and listen for TCP requests. 
The server should not run in a container. It should be able to be started with a 'java -jar TflyServer.jar'. The 
server should accept string requests and give string responses. I should be able to connect to the server and 
execute a request with something simple like telnet:

    > telnet localhost 4567
    telnet> my_request_string
  
## Included Service

Included with the exercise is a jar with a mock service, TflyService. This service will accept a string
and return the string reversed. The service might throw a TflyServiceException. If an exception is
thrown, the service should be retried.

## Message Sequence

The server response should include the string returned from the TflyService along with a response
sequence id that increments with every response.

If I execute two requests in telnet, I should see something like

    > telnet localhost 4567
    telnet> ticketfly

    ylftekcit 12
     
    telnet> is_rad
    
    dar_si 13

## Sequence Synchronization

A request string might have a sequence number after it separated by a space. If the sequence number is
larger than the current sequence number in the server, the server must reset its sequence number to at
least one higher than the passed in number.

If I execute a request including a sequence number, I should see something like

    > telnet localhost 4567
    telnet> ticketfly

    ylftekcit 12

    telnet> is_rad 789

    dar_si 790

    telnet> and_ticketmaster_isnt
    
    tnsi_retsamtekcet_dna 791

## Server Specification

  + The requests passed to the server will be dictionary words with no spaces or punctuation, except underscores are acceptable
  + The server must handle concurrent requests
  + Feel free to handle invalid requests in your own way

## Submission

Send the code and executable jar to aheadrick@ticketfly.com.


