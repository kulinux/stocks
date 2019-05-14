package com.stock.intrinio.stream

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}
import scala.concurrent.duration._

import scala.concurrent.{Await, Future}

class StreamSpec
  extends TestKit(ActorSystem("MySpec")) with WordSpecLike with  Matchers {


  val testSystem: ActorSystem = system

  val json = io.Source
    .fromInputStream(getClass.getResourceAsStream("/lit.json"))
    .mkString

  "Http" must {
    "Parse request" in {

      val parseHttp = new ParseNews {
        override implicit val system:ActorSystem = testSystem

        override def http(): Future[HttpResponse] = {

          val jsonH : HttpHeader = RawHeader("Content-Type", "application/json")


          Future.successful(HttpResponse(
            headers = scala.collection.immutable.Seq(jsonH),
            entity = HttpEntity(ContentTypes.`application/json`, json)
          ))
        }
      }

      implicit val mat = ActorMaterializer()
      val matFlow = parseHttp.news().to(Sink.foreach(x => println(s"New $x")))
      val fut = matFlow.run()

      val res = Await.result(fut, 10 seconds)

      Thread.sleep(10000)
    }

  }

}
