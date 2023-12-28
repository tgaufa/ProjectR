package com.example.projectr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectr.ui.ChatListScreen
import com.example.projectr.ui.LoginScreen
import com.example.projectr.ui.ProfileScreen
import com.example.projectr.ui.SignUpScreen
import com.example.projectr.ui.SingleChatScreen
import com.example.projectr.ui.SingleStatusScreen
import com.example.projectr.ui.StatusListScreen
import com.example.projectr.ui.theme.ProjectRTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreen(val route: String){
    object SignUp : DestinationScreen(route = "signup")
    object Login : DestinationScreen(route = "login")
    object Profile : DestinationScreen(route = "profile")
    object ChatList : DestinationScreen(route = "chatList")
    object SingleChat : DestinationScreen(route = "singleChat/{chatId}"){
        fun createRoute(id: String) = "singleChat/$id"
    }
    object StatusList : DestinationScreen(route = "statusList")
    object SingleStatus : DestinationScreen(route = "singleStatus/{statusId}"){
        fun createRoute(id: String) = "singleStatus/$id"
    }

}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectRTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation()
                }
            }
        }
    }
}

@Composable
fun ChatAppNavigation(){
    val navController = rememberNavController()
    val vm = hiltViewModel<ProjectRViewModel>()

    NotificationMessage(vm = vm)

    NavHost(navController =navController , startDestination = DestinationScreen.SignUp.route){
        composable(DestinationScreen.SignUp.route){
            SignUpScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Login.route){
            LoginScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Profile.route){
            ProfileScreen(navController = navController)
        }
        composable(DestinationScreen.StatusList.route){
            StatusListScreen(navController = navController)
        }
        composable(DestinationScreen.SingleStatus.route){
            SingleStatusScreen(statusId = "123")
        }
        composable(DestinationScreen.ChatList.route){
            ChatListScreen(navController = navController)
        }
        composable(DestinationScreen.SingleChat.route){
            SingleChatScreen(chatId = "123")
        }
    }
}