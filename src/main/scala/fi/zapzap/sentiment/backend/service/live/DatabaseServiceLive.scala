package fi.zapzap.sentiment.backend.service.live

import fi.zapzap.sentiment.backend.config.AppConfig
import fi.zapzap.sentiment.backend.service.DatabaseService
import scalikejdbc.{ConnectionPool, GlobalSettings, LoggingSQLAndTimeSettings}
import zio.blocking.Blocking
import zio.logging.{Logger, Logging}
import zio.{Has, Task, URLayer, ZLayer}

case class DatabaseServiceLive(config: AppConfig,
                               blockingService: Blocking.Service,
                               logger: Logger[String]) extends DatabaseService {
  override def connectDb(): Task[Unit] =
    blockingService.effectBlocking {
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
}

object DatabaseServiceLive {
  val layer: URLayer[Logging with Has[AppConfig] with Has[Blocking.Service], Has[DatabaseService]] =
    ZLayer.fromServices[AppConfig, Blocking.Service, Logger[String], DatabaseService] {
      (appConfig, blockingService, logger) => DatabaseServiceLive(appConfig, blockingService, logger)
    }
}