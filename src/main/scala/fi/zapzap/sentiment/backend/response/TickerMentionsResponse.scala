package fi.zapzap.sentiment.backend.response

import fi.zapzap.sentiment.backend.model.TickerMentionCount
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class TickerMentionsResponse(ticker: String, tickerMentions: Seq[TickerMentionCount])

object TickerMentionsResponse {
  implicit val decoder: JsonDecoder[TickerMentionsResponse] = DeriveJsonDecoder.gen[TickerMentionsResponse]
  implicit val encoder: JsonEncoder[TickerMentionsResponse] = DeriveJsonEncoder.gen[TickerMentionsResponse]
}

