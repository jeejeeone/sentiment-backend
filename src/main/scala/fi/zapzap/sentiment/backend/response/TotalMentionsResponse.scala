package fi.zapzap.sentiment.backend.response

import fi.zapzap.sentiment.backend.model.TotalMentionCount
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class TotalMentionsResponse(mentions: Seq[TotalMentionCount])

object TotalMentionsResponse {
  implicit val decoder: JsonDecoder[TotalMentionsResponse] = DeriveJsonDecoder.gen[TotalMentionsResponse]
  implicit val encoder: JsonEncoder[TotalMentionsResponse] = DeriveJsonEncoder.gen[TotalMentionsResponse]
}