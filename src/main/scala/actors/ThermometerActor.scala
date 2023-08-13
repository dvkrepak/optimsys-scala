package actors

import akka.actor.{Actor, ActorRef, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.util.Timeout
import api.ThermometerApi
import marshallers.ThermometerActionMarshaller
import play.api.libs.json.Json
import simulators.ThermometerAction


case class ThermometerActor(timeout: Timeout) extends Actor with ThermometerApi with ThermometerActionMarshaller {

  override def createMongoDbActor(): ActorRef = context.actorOf(Props(new MongoActor()))
  override implicit val requestTimeout: Timeout = timeout
  private val log: LoggingAdapter = Logging(context.system, this)

  override def receive: Receive = {
    case action: ThermometerAction =>
      saveData(Json.toJson(action).toString())
    case unexpectedMsg => log.error(s"ThermometerActor received an unexpected message: $unexpectedMsg")
  }
}
