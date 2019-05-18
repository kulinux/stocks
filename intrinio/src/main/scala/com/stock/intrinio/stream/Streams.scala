package com.stock.intrinio.stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.ActorMaterializer
import akka.stream.alpakka.json.scaladsl.JsonReader
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import com.stock.intrinio.model.{New, NewSentiment, StockPrice}
import com.stock.intrinio.sentiment.SentimentAnalyzer
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.Future


object Sentiments {
  val sentimentsFlow: Flow[New, NewSentiment, NotUsed] = {
    Flow[New]
      .map(x => NewSentiment(x, SentimentAnalyzer.mainSentiment(x.title)) )
  }
}

trait ParseNews extends Parse[New] {
  val frm = jsonFormat4(New)
  val path: String = "$.news[*]"
}

trait ParseStockPrice extends Parse[StockPrice] {
  val frm = jsonFormat13(StockPrice)
  val path: String = "$.stock_prices[*]"
}


trait Parse[T]  extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val system: ActorSystem

  val frm: RootJsonFormat[T]
  val path: String

  lazy implicit val um: Unmarshaller[ByteString, T] =
    SprayJsonSupport.sprayJsonByteStringUnmarshaller(frm)

  lazy implicit val materializer = ActorMaterializer()
  lazy implicit val executionContext = system.dispatcher

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()

  def http(): Future[HttpResponse]

  def source(): Source[T, Future[Any]] = {
    Source.fromFutureSource(fetchAndUnmarshall())
  }

  def fetchAndUnmarshall() = {
    http()
      .map(unmarshall)
  }

  def unmarshall(response: HttpResponse): Source[T, Any] = {
    return response.entity.
      dataBytes
        .log("Error!!!")
        .via(JsonReader.select(path))
        .mapAsync(1)( Unmarshal(_).to[T] )
  }

}
