import SeqCompActor.{LoadScript, SeqCompActorMsg, UnloadScript}
import akka.actor.CoordinatedShutdown
import akka.actor.CoordinatedShutdown.UnknownReason
import akka.actor.typed.SpawnProtocol.Spawn
import akka.actor.typed._
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object Main extends App {
    val actorSystem: ActorSystem[SpawnProtocol.Command] = ActorSystem(SpawnProtocol(), "seq-comp-system")
    implicit val timeout: Timeout = 10.seconds
    implicit val scheduler: Scheduler = actorSystem.scheduler

    val seqCompActor: ActorRef[SeqCompActorMsg] = Await.result(
      actorSystem ? (Spawn(SeqCompActor(), "seq-comp", Props.empty, _)),
      10.seconds
    )

    for (i <- 1 to 10000) {
      println(s"----------> iteration ${i}")
      Await.result(seqCompActor ? LoadScript, 2.seconds)
      Await.result(seqCompActor ? UnloadScript, 2.seconds)
    }
    Await.result(CoordinatedShutdown(actorSystem).run(UnknownReason), 2.seconds)
}
