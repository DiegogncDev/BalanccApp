package com.onedeepath.balanccapp.domain.model

import androidx.compose.ui.graphics.Color
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.ui.theme.EducationColor
import com.onedeepath.balanccapp.ui.theme.EntertainmentColor
import com.onedeepath.balanccapp.ui.theme.FoodColor
import com.onedeepath.balanccapp.ui.theme.GiftColor
import com.onedeepath.balanccapp.ui.theme.GroceryColor
import com.onedeepath.balanccapp.ui.theme.HealthColor
import com.onedeepath.balanccapp.ui.theme.InvestmentColor
import com.onedeepath.balanccapp.ui.theme.OtherColor
import com.onedeepath.balanccapp.ui.theme.PetColor
import com.onedeepath.balanccapp.ui.theme.RentColor
import com.onedeepath.balanccapp.ui.theme.TransportColor
import com.onedeepath.balanccapp.ui.theme.TravelColor
import com.onedeepath.balanccapp.ui.theme.UtilitiesColor
import com.onedeepath.balanccapp.ui.theme.WorkColor

enum class Category(
    val icon: Int,
    val color: Color
) {
    INVESTMENT(R.drawable.ic_investments, InvestmentColor),
    WORK(R.drawable.ic_work, WorkColor),
    GIFT(R.drawable.ic_gift, GiftColor),
    GROCERY(R.drawable.ic_grocery, GroceryColor),
    ENTERTAINMENT(R.drawable.ic_entertainment, EntertainmentColor),
    TRANSPORT(R.drawable.ic_transportation, TransportColor),
    UTILITIES(R.drawable.ic_utilities, UtilitiesColor),
    RENT(R.drawable.ic_rent, RentColor),
    HEALTH(R.drawable.ic_healt, HealthColor),
    TRAVEL(R.drawable.ic_travel, TravelColor),
    FOOD(R.drawable.ic_food, FoodColor),
    EDUCATION(R.drawable.ic_education, EducationColor),
    PET(R.drawable.ic_pet, PetColor),
    OTHER(R.drawable.ic_other, OtherColor)
}




