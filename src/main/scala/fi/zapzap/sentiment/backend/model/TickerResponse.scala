package fi.zapzap.sentiment.backend.model

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.time.LocalDate

case class TickerResponse(ticker: String, tickerMentions: Seq[TickerMentionCount])

case class TickerMentionCount(date: LocalDate, count: Int)

object TickerResponse {
  implicit val decoder: JsonDecoder[TickerResponse] = DeriveJsonDecoder.gen[TickerResponse]
  implicit val encoder: JsonEncoder[TickerResponse] = DeriveJsonEncoder.gen[TickerResponse]
}

object TickerMentionCount {
  implicit val decoder: JsonDecoder[TickerMentionCount] = DeriveJsonDecoder.gen[TickerMentionCount]
  implicit val encoder: JsonEncoder[TickerMentionCount] = DeriveJsonEncoder.gen[TickerMentionCount]
}