import SequencerActor.Shutdown
import akka.actor.typed.SpawnProtocol.Spawn
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed._
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object SeqCompActor {
  trait SeqCompActorMsg
  final case class LoadScript(replyTo: ActorRef[Loaded.type]) extends SeqCompActorMsg
  final case class UnloadScript(replyTo: ActorRef[Unloaded.type]) extends SeqCompActorMsg
  final case object Loaded
  final case object Unloaded

  var actorSystem: ActorSystem[SpawnProtocol.Command] = _
  var sequencerActor: ActorRef[Shutdown] = _
  lazy implicit val scheduler: Scheduler = actorSystem.scheduler
  implicit val timeout: Timeout = 10.seconds

  def apply(): Behavior[SeqCompActorMsg] = Behaviors.receive { (context, message) =>
    println(s"-------------> [SeqCompActor] received $message")
    message match {
      case LoadScript(replyTo) =>
        actorSystem = ActorSystem(Behaviors.empty, "sequencer1-system")
        sequencerActor = Await.result(
          actorSystem ? (Spawn(SequencerActor(), "sequencer-actor", Props.empty, _)),
          10.seconds
        )
        println("----------> [SeqCompActor] Sending Loaded Success response")
        replyTo ! Loaded
        Behaviors.same

      case UnloadScript(replyTo) =>
        sequencerActor ? Shutdown
        actorSystem.terminate()
        Await.result(actorSystem.whenTerminated, 10.seconds)
        println("----------> [SeqCompActor] Sending Unloaded Success response")
        replyTo ! Unloaded
        Behaviors.same
    }

  }
}
