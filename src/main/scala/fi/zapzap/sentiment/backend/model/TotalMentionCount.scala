package fi.zapzap.sentiment.backend.model

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class TotalMentionCount(ticker: String, sector: String, industry: String, count: Int)

object TotalMentionCount {
  implicit val decoder: JsonDecoder[TotalMentionCount] = DeriveJsonDecoder.gen[TotalMentionCount]
  implicit val encoder: JsonEncoder[TotalMentionCount] = DeriveJsonEncoder.gen[TotalMentionCount]
}