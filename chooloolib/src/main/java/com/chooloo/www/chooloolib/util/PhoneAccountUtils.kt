package com.chooloo.www.chooloolib.util

import android.telecom.PhoneAccount

// decoded number for tel: addresses ("+880..." not "%2B880..."), raw address otherwise
fun PhoneAccount.fullAddress(): String =
    address?.takeIf { it.scheme == PhoneAccount.SCHEME_TEL }?.schemeSpecificPart
        ?.takeIf { it.isNotEmpty() } ?: address.toString()

fun PhoneAccount.fullLabel(): String {
    val number = address?.takeIf { it.scheme == PhoneAccount.SCHEME_TEL }?.schemeSpecificPart
    return if (number.isNullOrEmpty()) label.toString() else "${label}(${number})"
}
