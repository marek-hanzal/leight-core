package rocks.leight.core.utils

import org.joda.time.DateTime

fun DateTime.asDate(): String = this.toString("YYYY-MM-dd")

fun DateTime.asStamp(): String = this.toString("YYYY-MM-dd HH:mm:ss.SSS")
