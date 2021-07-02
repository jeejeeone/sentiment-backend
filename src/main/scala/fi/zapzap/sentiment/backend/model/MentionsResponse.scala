package fi.zapzap.sentiment.backend.model

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class MentionsResponse(mentions: Seq[TotalMentionCount])

object MentionsResponse {
  implicit val decoder: JsonDecoder[MentionsResponse] = DeriveJsonDecoder.gen[MentionsResponse]
  implicit val encoder: JsonEncoder[MentionsResponse] = DeriveJsonEncoder.gen[MentionsResponse]
}