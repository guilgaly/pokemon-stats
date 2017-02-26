package pokestats.util

import java.time
import java.time.temporal.ChronoUnit

import scala.concurrent.{duration => scd}

object DurationConverters {
  import scala.language.implicitConversions

  implicit def asJavaDurationConverter(d: scd.Duration): AsJavaDuration =
    new AsJavaDuration(d)
  implicit def asScalaDurationConverter(d: time.Duration): AsScalaDuration =
    new AsScalaDuration(d)

  class AsJavaDuration(d: scd.Duration) {
    def asJava: time.Duration = time.Duration.of(d.toNanos, ChronoUnit.NANOS)
  }

  class AsScalaDuration(d: time.Duration) {
    def asScala: scd.Duration = scd.Duration.fromNanos(d.toNanos)
  }
}