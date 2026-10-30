package com.chooloo.www.chooloolib.util


object PhoneNumberUtils {
    fun isUri(number: String?) =
        number != null && (number.contains("@") || number.contains("%40"))

    fun isSecretCode(number: String) =
        number.length > 8 && number.startsWith("*#*#") && number.endsWith("#*#*")
}