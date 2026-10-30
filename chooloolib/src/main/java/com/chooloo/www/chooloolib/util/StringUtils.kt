package com.chooloo.www.chooloolib.util


fun String.initials() =
    split(' ').mapNotNull { it.firstOrNull()?.toString() }.joinToString("")

fun String.isRTL() = if (isEmpty()) {
    false
} else {
    val directionality = Character.getDirectionality(get(0))
    directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
            directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
}