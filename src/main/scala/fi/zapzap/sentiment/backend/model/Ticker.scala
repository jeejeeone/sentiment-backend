package fi.zapzap.sentiment.backend.model

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Ticker(ticker: String, industry: String, sector: String, marketCap: Int)

object Ticker {
  implicit val decoder: JsonDecoder[Ticker] = DeriveJsonDecoder.gen[Ticker]
  implicit val encoder: JsonEncoder[Ticker] = DeriveJsonEncoder.gen[Ticker]
}