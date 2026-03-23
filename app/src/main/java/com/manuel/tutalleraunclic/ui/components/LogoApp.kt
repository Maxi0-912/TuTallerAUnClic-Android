package com.manuel.tutalleraunclic.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.manuel.tutalleraunclic.R

@Composable
fun LogoApp() {

    Image(
        painter = painterResource(id = R.drawable.logo_solo),
        contentDescription = "Logo",
        modifier = Modifier.size(140.dp)
    )
}