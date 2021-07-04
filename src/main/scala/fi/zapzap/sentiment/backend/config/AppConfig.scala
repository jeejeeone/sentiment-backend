package fi.zapzap.sentiment.backend.config

import zio.{Has, Layer, ZLayer}
import zio.config.ConfigDescriptor._
import zio.config.ZConfig

case class AppConfig(dbHost: String,
                     dbPort: Int,
                     dbUser: String,
                     dbPassword: String,
                     dbDatabase: String)

object AppConfig {
  val configuration = (
      string("db.host")
      |@| int("db.port")
      |@| string("db.user")
      |@| string("db.password")
      |@| string("db.database")
    )(AppConfig.apply, AppConfig.unapply)

  val layer: Layer[Throwable, Has[AppConfig]] =
    ZConfig.fromPropertiesFile("sentiment-backend.properties", configuration).orElse {
      ZLayer.succeed(
        AppConfig(
          "localhost",
          5432,
          "postgres",
          "password",
          "postgres"
        ))
    }
}