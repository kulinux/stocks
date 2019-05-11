package com.stock.intrinio.http

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future
import scala.util.{Failure, Success}


//https://doc.akka.io/docs/akka-http/current/client-side/connection-level.html
object AkkaHttpIt extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher


  val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
    Http().outgoingConnection("akka.io")

  def dispatchRequest(request: HttpRequest): Future[HttpResponse] = {
    Source.single(request)
      .via(connectionFlow)
      .runWith(Sink.head)
  }

  val responseFuture: Future[HttpResponse] = dispatchRequest(HttpRequest(uri = "/"))

  responseFuture.andThen {
    case Success(response) => println(s"request succeeded $response")
    case Failure(_) => println("request failed")
  }.andThen {
    case _ => system.terminate()
  }


}
