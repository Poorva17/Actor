import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object SequencerActor {
  final case class Shutdown(replyTo: ActorRef[Success.type])
  final case object Success

  def apply(): Behavior[Shutdown] = Behaviors.receive { (context, message) =>
    println(s"************ [SequencerActor] received $message")
    println("*********** [SequencerActor] sending Shutdown success")
    message.replyTo ! Success
    Behaviors.stopped
  }
}
