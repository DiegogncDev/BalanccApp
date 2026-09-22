package com.onedeepath.balanccapp.core

// Month names as stored in the database and used in repository queries.
val ENGLISH_MONTHS = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

/**
 * Returns the (month index, year) pair resulting from moving [offset] months
 * from ([monthIndex], [year]), rolling over year boundaries.
 */
fun shiftedMonth(monthIndex: Int, year: String, offset: Int): Pair<Int, String> {
    val totalMonths = (year.toIntOrNull() ?: 0) * 12 + monthIndex + offset
    return (totalMonths % 12 + 12) % 12 to (totalMonths / 12).toString()
}
