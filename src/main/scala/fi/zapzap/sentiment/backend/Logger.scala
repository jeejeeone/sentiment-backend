package fi.zapzap.sentiment.backend

import zio.logging.{LogFormat, LogLevel, Logging}

object Logger {
  val logger = Logging.console(
    logLevel = LogLevel.Debug,
    format = LogFormat.ColoredLogFormat()
  ) >>> Logging.withRootLoggerName("sentiment-backend")
}
