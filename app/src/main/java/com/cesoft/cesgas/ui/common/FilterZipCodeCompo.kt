package com.cesoft.cesgas.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.cesoft.cesgas.R
import com.cesoft.cesgas.ui.theme.SepMax
import com.cesoft.cesgas.ui.theme.SepMed
import com.cesoft.cesgas.ui.theme.SepMin

//TODO: Allow several ZipCodes, separated automatically by long = 5
@Composable
fun FilterZipCodeCompo(
    isVisible: MutableState<Boolean>,
    zipCode: String,
    onSave: (String) -> Unit
) {
    val input = remember { mutableStateOf(zipCode) }
    val error = remember { mutableStateOf("") }
    val txtTitle = stringResource(R.string.zip_code)
    val txtError = stringResource(R.string.zip_code)
    if (isVisible.value) {
        Surface(shadowElevation = SepMin) {
            Row(
                verticalAlignment = CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = SepMax)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier.clickable { isVisible.value = false }
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(.5f)
                ) {
                    OutlinedTextField(
                        label = { Text(txtTitle) },
                        modifier = Modifier,
                        value = input.value,
                        maxLines = 1,
                        onValueChange = {
                            if(it.isDigitsOnly()) {
                                if(it.length <= 5) input.value = it
                            }
                            if(it.length == 5) error.value = ""
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = error.value.isNotBlank(),
                        supportingText = { Text(text = error.value) }
                    )
                }
                Button(
                    onClick = {
                        if(input.value.length == 5) onSave(input.value)
                        else error.value = txtError
                    },
                    modifier = Modifier.padding(horizontal = SepMed)
                ) { Text(text = stringResource(R.string.apply)) }
            }
        }
    }
    else {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AddItemButton(txtTitle, Modifier.width(150.dp)) { isVisible.value = true }
            if(zipCode.isNotBlank()) {
                SpendableCompo(
                    text = zipCode,
                    onClose = { onSave("") }
                )
            }
        }
    }
}

@Preview
@Composable
private fun FilterZipCodeCompo_closed_Preview() {
    val isVisible = remember { mutableStateOf(false) }
    Surface {
        FilterZipCodeCompo(isVisible, "28052") {}
    }
}

@Preview
@Composable
private fun FilterZipCodeCompo_open_Preview() {
    val isVisible = remember { mutableStateOf(true) }
    Surface {
        FilterZipCodeCompo(isVisible, "28052") {}
    }
}
