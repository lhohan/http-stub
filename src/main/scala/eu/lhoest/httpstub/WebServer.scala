package eu.lhoest.httpstub

import java.nio.file.{Path, Paths}

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.HttpApp
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO
import com.typesafe.config.ConfigFactory
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.io.Source
import scala.util.Try

object Main extends App {
  private val config = ConfigFactory.load()
  private val host = config.getString("http.host")
  private val port = config.getInt("http.port")

  WebServer.startServer(host, port)
}

object WebServer extends HttpApp with RepositoryFromFile with JsonSupport {
  repo: Repository =>

  import spray.json._

  private implicit val system = ActorSystem("webserver")
  private implicit val materializer = ActorMaterializer()

  import system.dispatcher

  override def routes =
    path("activate") {
      get {
        val jsonIds = Ids(repo.ids).toJson.prettyPrint
        complete(HttpEntity(ContentTypes.`application/json`, jsonIds))
      }
    } ~ path("activate") {
      post {
        withoutSizeLimit {
          extractDataBytes { bytes =>
            def readFrom(path: Path): Report = {
              val fileSource = Source.fromFile(path.toFile)
              val source = fileSource.mkString("")
              fileSource.close()
              val jsonAst = source.parseJson // or JsonParser(source)
              jsonAst.convertTo[Report]
            }

            def handleMaybeReport = { maybeReport: Try[Report] =>
              def handleReport = { report: Report =>
                repo
                  .read(report.id)
                  .fold {
                    reject
                  } { responsePayload =>
                    complete(HttpEntity(ContentTypes.`application/json`,
                                        responsePayload))
                  }
              }

              maybeReport.fold(
                { _: Throwable =>
                  reject
                },
                handleReport
              )
            }

            val outputPath = {
              val workDir = System.getProperty("user.dir")
              Paths
                .get(workDir)
                .resolve(s"payload.${System.currentTimeMillis()}.txt")
            }

            // first we write to a file, then we read payload from file again and unmarshal to class
            val finishedWriting = bytes.runWith(FileIO.toPath(outputPath))
            val finishedReading =
              finishedWriting.map(_ => readFrom(outputPath))
            onComplete(finishedReading) { handleMaybeReport }
          }
        }
      }
    }

}

final case class Report(id: String, count: Long)
final case class Ids(ids: List[String])

trait JsonSupport extends DefaultJsonProtocol {
  implicit val reportFormat: RootJsonFormat[Report] = jsonFormat2(Report)
  implicit val idsFormat: RootJsonFormat[Ids] = jsonFormat1(Ids)
}

trait Repository {
  def read(id: String): Option[String]
  def ids: List[String]
}

trait RepositoryFromFile extends Repository {

  val store: Map[String, String] = {
    val list = Source.fromResource("repository.txt").getLines().toList
    val lines = list.filter(_.nonEmpty).filterNot(_.trim.startsWith("#"))
    val pairs = lines
      .grouped(2)
      .toList
      .map(listOf2 => (listOf2(0), listOf2(1))) // linter:ignore UseHeadNotApply
    pairs.toMap
  }

  def read(id: String): Option[String] = {
    store.get(id)
  }

  def ids: List[String] = {
    store.keys.toList
  }

}
