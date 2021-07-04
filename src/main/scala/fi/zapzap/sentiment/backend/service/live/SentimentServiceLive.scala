package fi.zapzap.sentiment.backend.service.live

import fi.zapzap.sentiment.backend.config.AppConfig
import fi.zapzap.sentiment.backend.model.IntervalValue.IntervalValue
import fi.zapzap.sentiment.backend.model.{TickerMentionCount, TotalMentionCount}
import fi.zapzap.sentiment.backend.service.SentimentService
import scalikejdbc.{ConnectionPool, GlobalSettings, LoggingSQLAndTimeSettings}
import zio.blocking.Blocking
import zio.logging.{Logger, Logging}
import zio.{Has, Task, URLayer, ZIO, ZLayer}

import java.time.LocalDate

case class SentimentServiceLive(config: AppConfig,
                                blockingService: Blocking.Service,
                                logger: Logger[String]) extends SentimentService {
  override def connectDb(): Task[Unit] = blockingService.effectBlocking {
    logger.info(s"Connected to db: $config")

    // Disable logging
    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
      enabled = false,
      singleLineMode = false,
      printUnprocessedStackTrace = false,
      stackTraceDepth= 15,
      logLevel = Symbol("debug"),
      warningEnabled = false,
      warningThresholdMillis = 3000L,
      warningLogLevel = Symbol("warn")
    )

    Class.forName("org.postgresql.Driver")

    ConnectionPool.singleton(
      s"jdbc:postgresql://${config.dbHost}:${config.dbPort}/${config.dbDatabase}",
      config.dbUser,
      config.dbPassword
    )

    logger.info(s"Connected to db: $config")
    ()
  }

  override def totalMentionCount(interval: IntervalValue): Task[Seq[TotalMentionCount]] =
    ZIO.succeed(Seq(TotalMentionCount("MNMD", "Shroomstry", "Shrooms", 12123, 5)))

  override def tickerMentions(ticker: String): Task[Seq[TickerMentionCount]] =
    ZIO.succeed(Seq(TickerMentionCount(LocalDate.now(), 2)))
}

object SentimentServiceLive {
  val layer: URLayer[Logging with Has[AppConfig] with Has[Blocking.Service], Has[SentimentService]] =
    ZLayer.fromServices[AppConfig, Blocking.Service, Logger[String], SentimentService] {
      (appConfig, blockingService, logger) => SentimentServiceLive(appConfig, blockingService, logger)
    }
}