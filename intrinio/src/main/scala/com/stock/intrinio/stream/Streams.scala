package com.stock.intrinio.stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Graph, SourceShape}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.stock.intrinio.model.{Company, Item, New}
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future

class Streams {
  def news(id: String): Source[New, _] = ???
}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat2(Item)
  implicit val newsFormat = jsonFormat4(New)
  implicit val companyFormat = jsonFormat5(Company)
}

trait ParseHttpArray extends JsonSupport {

  implicit val system: ActorSystem
  lazy implicit val materializer = ActorMaterializer()
  lazy implicit val executionContext = system.dispatcher

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()

  def http(): Future[HttpResponse]

  def news(): Source[New, _] = {
    Source.fromFutureSource(newsFut())
  }

  def newsFut() = {
    for{
      response <- http()
      entsFut <- unmarshall(response)
    } yield entsFut
  }

  def unmarshall(response: HttpResponse) = {
    Unmarshal(response).to[Source[New, NotUsed]]
  }



}
/*

class ReadHttp(implicit aSystem: ActorSystem,
               url: String,
               key: String ) extends ParseHttpArray {

  override val system = aSystem



  def http(): Future[HttpResponse] =
    Http().singleRequest(
      HttpRequest(uri = url,
        headers = HttpHeader("X-Authorization-Public-Key", key) :: Nil
      ))

}

 */
