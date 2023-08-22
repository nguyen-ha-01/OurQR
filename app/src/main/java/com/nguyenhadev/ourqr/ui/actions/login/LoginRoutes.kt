package com.nguyenhadev.ourqr.ui.actions.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nguyenhadev.ourqr.Route
import com.nguyenhadev.ourqr.vm.LoginViewModel

fun NavGraphBuilder.loginRoutes(vm:LoginViewModel, controller: NavHostController){
    composable(Route.LoginRoute.route){
        LoginView(vm = vm, controller = controller)
    }
}