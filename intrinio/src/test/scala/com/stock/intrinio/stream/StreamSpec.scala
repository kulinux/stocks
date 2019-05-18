package com.stock.intrinio.stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
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

  def testParser[T](source: Source[T, _], f: T => Unit): Unit = {

    implicit val mat = ActorMaterializer()
    val matFlow = source
      .toMat(Sink.foreach(f))(Keep.right)


    val fut = matFlow.run()

    val res = Await.result(fut, 1000 seconds)
    println(s"res $res")


  }


  "Http" must {
    "Parse request news" in {

      val parseHttp = new ParseNews with ParseWithHttp[New] {
        val file = "/new_apple.json"
        override implicit val system:ActorSystem = testSystem
      }

      testParser[New](parseHttp.source(),
        x => println(s"${x.title}") )
    }
  }


  "Http" must {
    "Parse request stock prices" in {

      val parseHttp = new ParseStockPrice with ParseWithHttp[StockPrice] {
        val file = "/prices_apple.json"
        override implicit val system:ActorSystem = testSystem
      }

      testParser[StockPrice](parseHttp.source(),
        x => println(s"${x.date} ${x.high}") )
    }
  }


}
