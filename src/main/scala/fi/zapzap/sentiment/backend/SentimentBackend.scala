package fi.zapzap.sentiment.backend

import fi.zapzap.sentiment.backend.Env.SentimentBackend
import fi.zapzap.sentiment.backend.config.AppConfig
import fi.zapzap.sentiment.backend.model.IntervalValue
import fi.zapzap.sentiment.backend.response.GeneralErrorResponse.generalErrorResponse
import fi.zapzap.sentiment.backend.response.{TickerMentionsResponse, TotalMentionsResponse}
import fi.zapzap.sentiment.backend.service.SentimentService
import fi.zapzap.sentiment.backend.service.live.SentimentServiceLive
import fi.zapzap.sentiment.backend.util.Logger.logger
import zhttp.http._
import zhttp.service.Server
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.json.EncoderOps
import zio.logging.Logging
import zio.logging.Logging.{info, warn}
import zio.magic.ZioProvideMagicOps

object SentimentBackend extends App {
  val app: HttpApp[SentimentBackend, Nothing] = Http.collectM[Request] {
    request => request match {
      case Method.GET -> Root / "api" / "ui" / "mentions" / "last" / last =>
        IntervalValue.withNameOpt(last) match {
          case Some(interval) =>
            ZIO.serviceWith[SentimentService](_.totalMentionCount(interval))
              .map(TotalMentionsResponse(_))
              .map(_.toJson)
              .map(Response.jsonString)
              .catchAll(exc => handleError(request, Some(exc)))
          case _ =>
            handleError(
              request,
              None,
              s"Invalid input, use values ${IntervalValue.values.mkString(",")}"
            )
        }
      case Method.GET -> Root / "api" / "ui" / "mentions" / "ticker" / ticker / "day-interval" / days =>
        for {
          //TODO: Very awkward here, make it so that there are no intermediate variables and
          //      create correspending error responses according to relevant throwable
         days <- ZIO.effect(days.toInt.min(365).max(0)).orElse(ZIO.succeed(1))
         response <- ZIO.serviceWith[SentimentService](_.tickerMentions(ticker, days))
           .map(TickerMentionsResponse(ticker, _))
           .map(_.toJson)
           .map(Response.jsonString)
           .catchAll(exc => handleError(request, Some(exc)))
        } yield response
    }
  }

  def handleError(request: Request,
                  exception: Option[Throwable],
                  msg: String = "Failed to process request"): URIO[Logging, UResponse] =
    //TODO: Stacktrace
    warn(
      s"Failed to process request: " + s"path='${request.url.path.asString}', msg='$msg', exception=$exception"
    ) *> generalErrorResponse(msg)

  val start: URIO[SentimentBackend, ExitCode] =
    for {
      _        <- info("Starting sentiment backend")
      _        <- ZIO.serviceWith[SentimentService](_.connectDb()).orDie
      exitCode <- Server.start(8090, app.silent).exitCode
    } yield exitCode

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    start.inject(
      Blocking.live,
      Clock.live,
      logger,
      Console.live,
      AppConfig.layer,
      SentimentServiceLive.layer
    ).exitCode
}

object Env {
  type LibEnv = Clock with Blocking with Logging with Console

  type AppEnv = Has[SentimentService] with Has[AppConfig]

  type SentimentBackend = LibEnv with AppEnv
}