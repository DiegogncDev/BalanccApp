package com.onedeepath.balanccapp.ui.presentation.model

sealed class MonthsCard (
    val monthName: String,
    val incomeAmount: Double = 0.0,
    val expenseAmount: Double = 0.0,

) {
    object JanuaryCard : MonthsCard(monthName = "January")
    object FebruaryCard : MonthsCard(monthName = "February")
    object MarchCard : MonthsCard(monthName = "March")
    object AprilCard : MonthsCard(monthName = "April")
    object MayCard : MonthsCard(monthName = "May")
    object JuneCard : MonthsCard(monthName = "June")
    object JulyCard : MonthsCard(monthName = "July")
    object AugustCard : MonthsCard(monthName = "August")
    object SeptemberCard : MonthsCard(monthName = "September")
    object OctoberCard : MonthsCard(monthName = "October")
    object NovemberCard : MonthsCard(monthName = "November")
    object DecemberCard : MonthsCard(monthName = "December")
}