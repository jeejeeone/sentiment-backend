package fi.zapzap.sentiment.backend

import fi.zapzap.sentiment.backend.Env.SentimentBackend
import fi.zapzap.sentiment.backend.config.AppConfig
import fi.zapzap.sentiment.backend.model.IntervalValue
import fi.zapzap.sentiment.backend.response.{TickerMentionsResponse, TotalMentionsResponse}
import fi.zapzap.sentiment.backend.service.live.{DatabaseServiceLive, SentimentServiceLive}
import fi.zapzap.sentiment.backend.service.{DatabaseService, SentimentService}
import fi.zapzap.sentiment.backend.util.Logger.logger
import fi.zapzap.sentiment.backend.util.SentimentResponse.{toJsonResponse, withDefaultErrorHandling}
import zhttp.http._
import zhttp.service.Server
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.logging.Logging
import zio.logging.Logging.info
import zio.magic.ZioProvideMagicOps

object SentimentBackend extends App {
  val app: HttpApp[SentimentBackend, Nothing] = Http.collectM[Request] {
    request => request match {
      case Method.GET -> Root / "api" / "ui" / "mentions" / "last" / last =>
        // TODO: How to pass implicit request instead of param?
        withDefaultErrorHandling(request) {
          IntervalValue.withNameOpt(last) match {
            case Some(interval) =>
              ZIO.serviceWith[SentimentService](_.totalMentionCount(interval))
                .map(TotalMentionsResponse(_))
                .map(toJsonResponse(_)(TotalMentionsResponse.encoder))
            case _ =>
              ZIO.fail(new RuntimeException(s"Invalid input, use values ${IntervalValue.values.mkString(",")}"))
          }
        }
      case Method.GET -> Root / "api" / "ui" / "mentions" / "ticker" / ticker  =>
        withDefaultErrorHandling(request) {
          ZIO.serviceWith[SentimentService](_.tickerMentions(ticker))
            .map(TickerMentionsResponse(ticker, _))
            .map(toJsonResponse(_)(TickerMentionsResponse.encoder))
        }
    }
  }

  val start: URIO[SentimentBackend, Unit] =
    for {
      _        <- info("Starting sentiment backend")
      _        <- ZIO.serviceWith[DatabaseService](_.connectDb()).orDie
      _        <- Server.start(8090, app.silent).catchAll(_ => info("Failed to start server"))
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    start.inject(
      Blocking.live,
      Clock.live,
      logger,
      Console.live,
      AppConfig.layer,
      SentimentServiceLive.layer,
      DatabaseServiceLive.layer
    ).exitCode
}

object Env {
  type LibEnv = Clock with Blocking with Logging with Console

  type AppEnv = Has[SentimentService] with Has[AppConfig] with Has[DatabaseService]

  type SentimentBackend = LibEnv with AppEnv
}