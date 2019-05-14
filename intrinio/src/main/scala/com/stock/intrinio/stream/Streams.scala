package com.stock.intrinio.stream

import akka.actor.ActorSystem
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.alpakka.json.scaladsl.JsonReader
import akka.stream.scaladsl.Source
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

trait ParseNews extends JsonSupport {

  implicit val system: ActorSystem
  lazy implicit val materializer = ActorMaterializer()
  lazy implicit val executionContext = system.dispatcher

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()

  def http(): Future[HttpResponse]

  def news(): Source[New, Future[Any]] = {
    Source.fromFutureSource(newsFut())
  }

  def newsFut() = {
    http()
      .map(unmarshall)
  }

  def unmarshall(response: HttpResponse): Source[New, Any] = {
    return response.entity.
      dataBytes
        .via(JsonReader.select("$.news[*]"))
        .mapAsync(1)( Unmarshal(_).to[New])
  }

}
