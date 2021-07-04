package fi.zapzap.sentiment.backend.model

import scala.util.Try

object IntervalValue extends Enumeration {
  type IntervalValue = Value

  val TwentyFourHours = Value("24hours")
  val SevenDays = Value("7days")
  val ThirtyDays = Value("30days")
  val HundredDays = Value("100days")

  def withNameOpt(name: String): Option[IntervalValue] = Try {
    withName(name)
  }.toOption
}
