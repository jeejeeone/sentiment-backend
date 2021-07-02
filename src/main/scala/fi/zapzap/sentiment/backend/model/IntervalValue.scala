package fi.zapzap.sentiment.backend.model

object IntervalValue extends Enumeration {
  type IntervalValue = Value

  val TwentyFourHours, SevenDays, ThirtyDays, HundredDays = Value

  val stringMap = Map(
    "24 hours" -> TwentyFourHours,
    "7 days" -> SevenDays,
    "30 days" -> ThirtyDays,
    "100 days" -> HundredDays
  )

  def fromIntervalString(value: String): Option[Value] = stringMap.get(value)
}
