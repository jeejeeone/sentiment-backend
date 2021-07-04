package fi.zapzap.sentiment.backend.model

object IntervalValue extends Enumeration {
  type IntervalValue = Value

  val TwentyFourHours, SevenDays, ThirtyDays, HundredDays = Value

  val stringMap = Map(
    "24hours" -> TwentyFourHours,
    "7days" -> SevenDays,
    "30days" -> ThirtyDays,
    "100days" -> HundredDays
  )

  def fromIntervalString(value: String): Option[Value] = stringMap.get(value)
}
