package com.onedeepath.balanccapp.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.ui.navigation.AppScreens

@Composable
fun SplashScreen(navController: NavController) {
    // Navigate to the main screen after a delay or any essencial operation
    LaunchedEffect(key1 = true) {

        navController.popBackStack() // Remove the splash screen from the back stack to prevent going back
        navController.navigate(AppScreens.MainScreen.route)
    }



    Splash()
}

@Composable
fun Splash() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_balance),
            contentDescription = "Logo",
            Modifier.size(150.dp, 150.dp)
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    Splash()
}