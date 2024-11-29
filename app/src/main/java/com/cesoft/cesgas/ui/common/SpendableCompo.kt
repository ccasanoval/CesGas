package com.cesoft.cesgas.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.cesoft.cesgas.R
import com.cesoft.cesgas.ui.theme.CylinderShape
import com.cesoft.cesgas.ui.theme.SepMax
import com.cesoft.cesgas.ui.theme.SepMed
import com.cesoft.cesgas.ui.theme.SepMin


@Composable
fun SpendableCompo(
    text: String,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    Button(
        contentPadding = PaddingValues(start = SepMax, end = SepMed),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.inversePrimary,
            containerColor = MaterialTheme.colorScheme.inverseSurface,
        ),
        shape = CylinderShape,
        onClick = onClose,
        modifier = modifier.padding(horizontal = SepMin),
        content = {
            Text(text)
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.delete),
                modifier = Modifier.padding(start = SepMin)
            )
        }
    )
}

//--------------------------------------------------------------------------------------------------
@Preview
@Composable
private fun SpendableCompo_Preview() {
    SpendableCompo("Aaaaa") {}
}