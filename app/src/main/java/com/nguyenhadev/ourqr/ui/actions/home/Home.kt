package com.nguyenhadev.ourqr.ui.actions.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nguyenhadev.ourqr.BottomBar
import com.nguyenhadev.ourqr.MainNavHost
import com.nguyenhadev.ourqr.Route
import com.nguyenhadev.ourqr.vm.MainViewModel

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier.fillMaxSize()){
    val controller = rememberNavController()
    val viewModel : MainViewModel = viewModel()

    Scaffold(modifier = modifier, bottomBar = {
        BottomBar(controller)
    }, snackbarHost = {
        LaunchedEffect(key1 = viewModel.snackBarHostState){

        }
        if(viewModel.snackBarHostState) {
            Snackbar (){
                Text(viewModel.snackBarData)
            }
        }
    }){

        MainNavHost(navController = controller, Modifier.padding(it), viewModel = viewModel)
    }
}

fun NavGraphBuilder.mainRoutes(){
    composable(Route.Home.route){
        MainScreen()
    }
}