package com.cesoft.cesgas.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoadingCompo(background: Boolean = true) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .alpha(if(background) 1f else .75f)
        //.background(Color.Transparent)
        //.clickable { android.util.Log.e("LoadingCompo", "Click disabled") }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

//
//    Column(
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier.fillMaxSize()
//    ) {
//        CircularProgressIndicator()
//    }
}

@Preview
@Composable
private fun LoadingCompo_Preview() {
    Surface(Modifier.height(200.dp)) {
        Column {
            Text("AAAAAAA")
            Text("BBBBBBB")
            Text("CCCCCCC")
        }
        LoadingCompo(false)
    }
}
