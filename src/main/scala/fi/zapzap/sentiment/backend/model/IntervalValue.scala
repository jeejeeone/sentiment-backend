package fi.zapzap.sentiment.backend.model

import scala.util.Try

object IntervalValue extends Enumeration {
  type IntervalValue = Value

  val TwentyFourHours = Value("24 hours")
  val SevenDays = Value("7 days")
  val ThirtyDays = Value("30 days")
  val HundredDays = Value("100 days")

  def withNameOpt(name: String): Option[IntervalValue] = Try {
    withName(name)
  }.toOption
}
