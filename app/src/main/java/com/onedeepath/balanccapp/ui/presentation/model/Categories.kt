package com.onedeepath.balanccapp.ui.presentation.model

import android.graphics.drawable.Icon
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

sealed class Categories(
    val name: String,
    val icon: Int,
    val color: Color
) {
    object Investment : Categories("Investments", R.drawable.ic_investments, InvestmentColor)
    object Work : Categories("Work", R.drawable.ic_work, WorkColor)
    object Gift : Categories("Gift", R.drawable.ic_gift, GiftColor)
    object Grocery : Categories("Grocery", R.drawable.ic_grocery, GroceryColor)
    object Entertainment : Categories("Entertainment", R.drawable.ic_entertainment, EntertainmentColor)
    object Transport : Categories("Transport", R.drawable.ic_transportation, TransportColor)
    object Utilities : Categories("Utilities", R.drawable.ic_utilities, UtilitiesColor)
    object Rent : Categories("Rent", R.drawable.ic_rent, RentColor)
    object Health : Categories("Health", R.drawable.ic_healt, HealthColor)
    object Travel : Categories("Travel", R.drawable.ic_travel, TravelColor)
    object Food : Categories("Food", R.drawable.ic_food, FoodColor)
    object Education : Categories("Education", R.drawable.ic_education, EducationColor)
    object Pet: Categories("Pet", R.drawable.ic_pet, PetColor)
    object Other : Categories("Other", R.drawable.ic_other, OtherColor)

}