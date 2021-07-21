package fi.zapzap.sentiment.backend.service.live

import fi.zapzap.sentiment.backend.config.AppConfig
import fi.zapzap.sentiment.backend.model.IntervalValue.IntervalValue
import fi.zapzap.sentiment.backend.model.{TickerMentionCount, TotalMentionCount}
import fi.zapzap.sentiment.backend.service.SentimentService
import fi.zapzap.sentiment.backend.util.SafeSQLSyntax.safeSQLSyntax
import scalikejdbc.{ConnectionPool, _}
import zio.blocking.Blocking
import zio.logging.{Logger, Logging}
import zio.{Has, Task, URLayer, ZLayer}

case class SentimentServiceLive(config: AppConfig,
                                blockingService: Blocking.Service,
                                logger: Logger[String]) extends SentimentService {
  override def connectDb(): Task[Unit] = blockingService.effectBlocking {
    // Disable logging
    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
      enabled = false,
      singleLineMode = false,
      printUnprocessedStackTrace = false,
      stackTraceDepth = 15,
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
  }

  override def totalMentionCount(interval: IntervalValue): Task[Seq[TotalMentionCount]] =
    blockingService.effectBlocking {
      DB readOnly { implicit session =>
        sql"""
             SELECT mention, COUNT(*)
             FROM mentions
             WHERE time > NOW() - interval '${safeSQLSyntax(interval.toString)}'
             GROUP BY mention
             ORDER BY count DESC;
           """
          .map(rs =>
            TotalMentionCount(
              rs.string("mention"),
              //TODO: Join with industry and sector data
              "shrooms",
              "shroomery",
              1,
              rs.int("count")
            )
          ).list().apply()
      }
    }
  
  override def tickerMentions(ticker: String, dayInterval: Int): Task[Seq[TickerMentionCount]] =
    blockingService.effectBlocking {
      DB readOnly { implicit session =>
        sql"""
             SELECT time_bucket('1 day', time) AS date, mention, COUNT(*)
             FROM mentions
             WHERE
               time > NOW() - interval '${safeSQLSyntax("500days")}'
             AND
               mention = $ticker
             GROUP BY date, mention
             ORDER BY date, count DESC;
           """
          .map(rs =>
            TickerMentionCount(
              rs.localDate("date"),
              rs.int("count")
            )
          ).list().apply()
      }
    }
}

object SentimentServiceLive {
  val layer: URLayer[Logging with Has[AppConfig] with Has[Blocking.Service], Has[SentimentService]] =
    ZLayer.fromServices[AppConfig, Blocking.Service, Logger[String], SentimentService] {
      (appConfig, blockingService, logger) => SentimentServiceLive(appConfig, blockingService, logger)
    }
}