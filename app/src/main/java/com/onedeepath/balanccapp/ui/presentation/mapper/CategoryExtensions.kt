package com.onedeepath.balanccapp.ui.presentation.mapper

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.domain.model.Category
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

@StringRes
fun Category.getDisplayNameRes(): Int = when (this) {
    Category.INVESTMENT -> R.string.investment
    Category.WORK -> R.string.work
    Category.GIFT -> R.string.gift
    Category.GROCERY -> R.string.grocery
    Category.ENTERTAINMENT -> R.string.entertainment
    Category.TRANSPORT -> R.string.transport
    Category.UTILITIES -> R.string.utilities
    Category.RENT -> R.string.rent
    Category.HEALTH -> R.string.health
    Category.TRAVEL -> R.string.travel
    Category.FOOD -> R.string.food
    Category.EDUCATION -> R.string.education
    Category.PET -> R.string.pet
    Category.OTHER -> R.string.other
}

@DrawableRes
fun Category.getIconRes(): Int = when (this) {
    Category.INVESTMENT -> R.drawable.ic_investments
    Category.WORK -> R.drawable.ic_work
    Category.GIFT -> R.drawable.ic_gift
    Category.GROCERY -> R.drawable.ic_grocery
    Category.ENTERTAINMENT -> R.drawable.ic_entertainment
    Category.TRANSPORT -> R.drawable.ic_transportation
    Category.UTILITIES -> R.drawable.ic_utilities
    Category.RENT -> R.drawable.ic_rent
    Category.HEALTH -> R.drawable.ic_healt
    Category.TRAVEL -> R.drawable.ic_travel
    Category.FOOD -> R.drawable.ic_food
    Category.EDUCATION -> R.drawable.ic_education
    Category.PET -> R.drawable.ic_pet
    Category.OTHER -> R.drawable.ic_other
}

fun Category.getColor(): Color = when (this) {
    Category.INVESTMENT -> InvestmentColor
    Category.WORK -> WorkColor
    Category.GIFT -> GiftColor
    Category.GROCERY -> GroceryColor
    Category.ENTERTAINMENT -> EntertainmentColor
    Category.TRANSPORT -> TransportColor
    Category.UTILITIES -> UtilitiesColor
    Category.RENT -> RentColor
    Category.HEALTH -> HealthColor
    Category.TRAVEL -> TravelColor
    Category.FOOD -> FoodColor
    Category.EDUCATION -> EducationColor
    Category.PET -> PetColor
    Category.OTHER -> OtherColor
}

fun Category.getColorInt(): Int = getColor().toArgb()
