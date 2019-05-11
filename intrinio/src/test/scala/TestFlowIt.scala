import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source, SourceQueueWithComplete}

import scala.concurrent.Future

class SinkActor extends Actor {
  override def receive: Receive = {
    case msg => println(s"$msg")
  }
}


object TestFlowIt extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val disp = system.dispatcher
  val source: Source[Int, _] = Source(1 to 1000)
  val sink: Sink[Int, Future[String]] = Sink.fold[String, Int]("0")( _ + _ )

  val actorRef = system.actorOf(Props[SinkActor])
  val sinkAr: Sink[Int, _] = Sink.actorRef(actorRef, "COMPLETE")

  val flow1: Flow[Int, Int, _] = Flow[Int].map(_ + 10)

  val resFut: Future[String] = source.runWith(sink)

  val queue: Source[Int, SourceQueueWithComplete[Int]] = Source.queue[Int](100, OverflowStrategy.fail)

  for{
    res: String  <- resFut
  } println(res)

}

object Test2 extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  val source : Source[Int, _] = Source
    .fromIterator(() => Iterator.range(0, 100))

  val fut = Future.successful(source)

  val res = Source.fromFutureSource(fut)

  val graph = res.to(Sink.foreach(println))

  graph.run()
}
