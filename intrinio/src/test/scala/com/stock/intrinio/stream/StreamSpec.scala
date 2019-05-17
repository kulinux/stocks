package com.stock.intrinio.stream

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink}
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Success}

class StreamSpec
  extends TestKit(ActorSystem("MySpec")) with WordSpecLike with  Matchers {


  val testSystem: ActorSystem = system

  //val File = "/lit.json"
  val File = "/news2.json"

  val json = io.Source
    .fromInputStream(getClass.getResourceAsStream(File))
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
      val matFlow = parseHttp
        .news()
        .log("Error!!!!!")
        .toMat(Sink.foreach(x => println(s"${x.sentiment} - ${x.item.title}")))(Keep.right)


      val fut = matFlow.run()

      val res = Await.result(fut, 1000 seconds)
      println(s"res $res")

      //Thread.sleep(100000)
    }

  }

}
