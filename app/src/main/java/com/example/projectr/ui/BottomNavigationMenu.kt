package com.example.projectr.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.projectr.DestinationScreen
import com.example.projectr.R
import com.example.projectr.navigateTo

enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen){
    STATUSLIST(R.drawable.baseline_status, DestinationScreen.StatusList),
    PROFILE(R.drawable.baseline_profile, DestinationScreen.Profile),
    CHATLIST(R.drawable.baseline_chat, DestinationScreen.ChatList)


}

@Composable
fun BottomNavigationMenu(selectedItem: BottomNavigationItem, navController: NavController){
    Row (modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(top = 4.dp))
    {
        for (item in BottomNavigationItem.values()){
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
                    .weight(1f)
                    .clickable {
                               navigateTo(navController, item.navDestination.route)

                    },
                colorFilter = if(item == selectedItem) ColorFilter.tint(Color.Black)
                else ColorFilter.tint(Color.Gray)
                )
        }
    }
}