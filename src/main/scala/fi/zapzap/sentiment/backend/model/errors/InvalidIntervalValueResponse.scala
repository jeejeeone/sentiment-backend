package fi.zapzap.sentiment.backend.model.errors

import fi.zapzap.sentiment.backend.model.IntervalValue
import zhttp.http.HttpError.InternalServerError
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, EncoderOps, JsonDecoder, JsonEncoder}

case class InvalidIntervalValueError(error: String, validValues: Seq[String]) extends Error

object InvalidIntervalValueResponse {
  def invalidIntervalValueResponse(): InternalServerError =
    InternalServerError(
      InvalidIntervalValueError("Invalid interval value", IntervalValue.stringMap.keys.toSeq)
        .toJson
    )
}

object InvalidIntervalValueError {
  implicit val decoder: JsonDecoder[InvalidIntervalValueError] = DeriveJsonDecoder.gen[InvalidIntervalValueError]
  implicit val encoder: JsonEncoder[InvalidIntervalValueError] = DeriveJsonEncoder.gen[InvalidIntervalValueError]
}
