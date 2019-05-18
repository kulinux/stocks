package com.stock.intrinio.stream

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink}
import akka.testkit.TestKit
import com.stock.intrinio.model.{New, StockPrice}
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait ParseWithHttp[T] extends Parse[T] {

  val file: String

  override def http(): Future[HttpResponse] = {
    val jsonH : HttpHeader = RawHeader("Content-Type", "application/json")

    val json = io.Source
      .fromInputStream(getClass.getResourceAsStream(file))
      .mkString

    Future.successful(HttpResponse(
      headers = scala.collection.immutable.Seq(jsonH),
      entity = HttpEntity(ContentTypes.`application/json`, json)
    ))
  }
}

class StreamSpec
  extends TestKit(ActorSystem("MySpec")) with WordSpecLike with  Matchers {


  implicit val testSystem: ActorSystem = system

  val File = "/lit.json"
  //val File = "/new_apple.json"


  "Http" must {
    "Parse request news" in {

      val parseHttp = new ParseNews with ParseWithHttp[New] {
        val file = "/lit.json"
        override implicit val system:ActorSystem = testSystem
      }

      implicit val mat = ActorMaterializer()
      val matFlow = parseHttp
        .source()
        .toMat(Sink.foreach(x => println(s"${x.title}")))(Keep.right)


      val fut = matFlow.run()

      val res = Await.result(fut, 1000 seconds)
      println(s"res $res")

    }

  }


  "Http" must {
    "Parse request stock prices" in {

      val parseHttp = new ParseStockPrice with ParseWithHttp[StockPrice] {
        val file = "/prices_apple.json"
        override implicit val system:ActorSystem = testSystem
      }

      implicit val mat = ActorMaterializer()
      val matFlow = parseHttp
        .source()
        .toMat(Sink.foreach(x => println(s"${x.date} ${x.high}")))(Keep.right)


      val fut = matFlow.run()

      val res = Await.result(fut, 1000 seconds)
      println(s"res $res")

    }

  }


}
