package rocks.leight.core.utils

import java.security.SecureRandom

fun String.Companion.randomNumber(length: Int): String {
    return SecureRandom().nextInt("9".repeat(length).toInt()).toString().padStart(length, '0')
}
