package fi.zapzap.sentiment.backend

import fi.zapzap.sentiment.backend.Env.SentimentBackend
import fi.zapzap.sentiment.backend.model.errors.InvalidIntervalValueResponse.invalidIntervalValueResponse
import fi.zapzap.sentiment.backend.model.{IntervalValue, MentionsResponse, TickerResponse}
import fi.zapzap.sentiment.backend.service.SentimentService
import zhttp.http._
import zhttp.service.Server
import zio._
import zio.json.EncoderOps
import zio.logging.Logging.info

object SentimentBackend extends App {
  // Create HTTP route
  val app: HttpApp[SentimentBackend, Nothing] = Http.collectM[Request] {
    case Method.GET -> Root / "mentions" / "last" / last => {
      IntervalValue.fromIntervalString(last) match {
        case Some(interval) =>
          for {
            response <- ZIO.serviceWith[SentimentService](_.totalMentionCount(interval))
                              .map(MentionsResponse(_))
                              .map(_.toJson)
                              .map(Response.jsonString)
                              .catchAll(_ => ZIO.succeed(Response.status(Status.INTERNAL_SERVER_ERROR)))
          } yield response
        case _ => ZIO.succeed(Response.fromHttpError(invalidIntervalValueResponse()))
      }
    }
    case Method.GET -> Root / "mentions" / "ticker" / ticker =>
      for {
        response <- ZIO.serviceWith[SentimentService](_.tickerMentions(ticker))
          .map(TickerResponse(ticker, _))
          .map(_.toJson)
          .map(Response.jsonString)
          .catchAll(_ => ZIO.succeed(Response.status(Status.INTERNAL_SERVER_ERROR)))
      } yield response
  }

  val start: URIO[SentimentBackend, ExitCode] =
    for {
      _        <- info("Starting sentiment backend")
      exitCode <- Server.start(8090, app.silent).exitCode
    } yield exitCode

  // Run it like any simple app
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    start
      .provideLayer(Env.appLayer)
}