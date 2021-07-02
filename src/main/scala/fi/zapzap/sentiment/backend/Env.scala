package fi.zapzap.sentiment.backend

import fi.zapzap.sentiment.backend.Logger.logger
import fi.zapzap.sentiment.backend.service.SentimentService
import fi.zapzap.sentiment.backend.service.live.SentimentServiceLive
import zio.Has
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.logging.Logging

object Env {
  val baseEnvLayer =
    (Blocking.live
      ++ Clock.live
      ++ logger
      ++ Console.live)

  val appLayer = baseEnvLayer ++ SentimentServiceLive.layer

  type LibEnv = Clock with Blocking with Logging with Console

  type AppEnv = Has[SentimentService]

  type SentimentBackend = LibEnv with AppEnv
}
