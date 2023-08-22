package com.nguyenhadev.ourqr.ui.actions.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nguyenhadev.ourqr.LoginHost
import com.nguyenhadev.ourqr.vm.LoginViewModel
@Preview()
@Composable
fun LoginHostV(){
    val controller =  rememberNavController()
    LoginHost(controller,  Modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(vm: LoginViewModel, controller: NavHostController){
    Surface (Modifier.background(colorScheme.background)){
        Column(
            Modifier
                .fillMaxSize()
                .background(colorScheme.background), horizontalAlignment = Alignment.CenterHorizontally) {
            var email by remember{
                mutableStateOf("")
            }
            var password by remember{
                mutableStateOf("")
            }

            Card() {
                OutlinedTextField(value = email, onValueChange = {it-> email = it}, maxLines = 1)
                OutlinedTextField(value = password, onValueChange = {it-> password = it}, 
                    maxLines = 1, keyboardActions = KeyboardActions(onSend = {
                        // TODO: check password 
                    }), label = { Text(text = "Password")}
                )
                
            }
        }
    }
}
