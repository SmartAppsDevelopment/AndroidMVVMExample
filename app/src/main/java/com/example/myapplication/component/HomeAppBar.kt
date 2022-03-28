package com.example.myapplication.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeandroidpractice.ui.theme.ComposeAndroidPracticeTheme
import com.example.myapplication.R
import com.example.myapplication.helper.sdp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun MyAppBar(
    scaffoldState: ScaffoldState? = null,
    scope: CoroutineScope? = null) {

    Box(modifier = Modifier.padding(8.dp)) {
        Card(
            shape = RoundedCornerShape(10.dp),
            elevation = 6.dp,
            modifier = Modifier.requiredHeight(50.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(onClick = {
                    scope?.launch {
                        scaffoldState?.drawerState?.open()
                    }

                }) {
                    Icon(Icons.Default.Menu, "", modifier = Modifier.weight(0.4f))

                }

                Text(fontSize = 18.sp,text = "Gmail Clone", modifier = Modifier.weight(2f), fontWeight = FontWeight.ExtraBold)

                Image(painter = painterResource(id = R.drawable.ic_baseline_image_24 ), contentDescription = "", modifier = Modifier
                    .size(20.sdp)
                    .weight(0.4f))
//                    .clickable { openDialog.value = true })
//                if(openDialog.value){
//                   /// AccountDialog(openDialog = openDialog)
//                }
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    ComposeAndroidPracticeTheme {

    }
}