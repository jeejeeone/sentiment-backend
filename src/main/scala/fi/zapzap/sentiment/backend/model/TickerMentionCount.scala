package fi.zapzap.sentiment.backend.model

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.time.LocalDate

case class TickerMentionCount(date: LocalDate, count: Int)

object TickerMentionCount {
  implicit val decoder: JsonDecoder[TickerMentionCount] = DeriveJsonDecoder.gen[TickerMentionCount]
  implicit val encoder: JsonEncoder[TickerMentionCount] = DeriveJsonEncoder.gen[TickerMentionCount]
}
