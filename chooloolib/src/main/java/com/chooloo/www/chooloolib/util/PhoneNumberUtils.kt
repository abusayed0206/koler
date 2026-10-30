package com.chooloo.www.chooloolib.util

import android.net.Uri


object PhoneNumberUtils {
    /** Number from a dial intent's data, e.g. "tel:+880..." or "tel:%2B880..." -> "+880..." (keeps the +). */
    fun numberFromTelUri(uri: Uri?): String =
        (if (uri?.scheme == "tel") uri.schemeSpecificPart else uri?.toString()?.let(Uri::decode)).orEmpty().trim()

    fun isUri(number: String?) =
        number != null && (number.contains("@") || number.contains("%40"))

    fun isSecretCode(number: String) =
        number.length > 8 && number.startsWith("*#*#") && number.endsWith("#*#*")
}