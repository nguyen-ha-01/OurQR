package com.nguyenhadev.ourqr

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nguyenhadev.ourqr.ui.actions.home.mainRoutes
import com.nguyenhadev.ourqr.ui.actions.login.loginRoutes
import com.nguyenhadev.ourqr.vm.LoginViewModel
import com.nguyenhadev.ourqr.vm.MainViewModel



@Composable
fun LoginHost(navController: NavHostController, modifier: Modifier){
    val loginViewModel :LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    NavHost(navController = navController,modifier = modifier, startDestination =  Route.LoginRoute.route ){
        loginRoutes(loginViewModel,  navController)
        mainRoutes()
    }
}
@Composable
fun MainNavHost(navController: NavHostController, modifier: Modifier, viewModel: MainViewModel) {

    NavHost(navController = navController, startDestination = BottomNavItem.Home.route, modifier){

        composable(BottomNavItem.Home.route){

            HomeScreen(Modifier.fillMaxSize(),navController,viewModel)
        }
        composable(BottomNavItem.MyQr.route){
            MyQrsScreen(navController,viewModel)
        }
        composable(Route.Scan.route){
            ScanScreen(navController,viewModel)
        }
        composable(Route.CreateQr.route){
            CreateQr(navController,viewModel)
        }


    }
}
sealed class BottomNavItem(val route: String, val res: Int , val title:String){
    object Home:BottomNavItem("Home",R.drawable.qr_code_scan, "Scan" )
    object MyQr:BottomNavItem("MyQr",R.drawable.folder,"QR")

}
sealed class Route(val route :String , val name :String ,var resId : Int = R.drawable.qr_code_scan){
    object Scan:Route("Scan","scan screen",   R.drawable.qr_code_scan)
    object CreateQr:Route("CreateQr","create qr screen",   R.drawable.page)
    object LoginRoute:Route(route = "Login", name = "Login screen")
    object Home:Route(route = "Home",  name = "screen after login success")
}