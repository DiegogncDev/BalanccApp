package com.onedeepath.balanccapp.ui.presentation.mapper

import androidx.annotation.StringRes
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.domain.model.Category

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
