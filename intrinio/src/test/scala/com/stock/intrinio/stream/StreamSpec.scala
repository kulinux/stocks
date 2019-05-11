package com.stock.intrinio.stream

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}

import scala.collection.parallel.immutable
import scala.concurrent.Future

class StreamSpec
  extends TestKit(ActorSystem("MySpec")) with WordSpecLike with  Matchers {


  val testSystem: ActorSystem = system

  val json = io.Source
    .fromInputStream(getClass.getResourceAsStream("/news2.json"))
    .mkString

  "Http" must {
    "Parse request" in {

      val parseHttp = new ParseHttp {
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
      val matFlow = parseHttp.news().to(Sink.foreach(println))
      matFlow.run()

      Thread.sleep(10000)
    }

  }

}
