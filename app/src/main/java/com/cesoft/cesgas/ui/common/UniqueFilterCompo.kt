package com.cesoft.cesgas.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.cesoft.cesgas.R
import com.cesoft.cesgas.ui.theme.CylinderShape
import com.cesoft.cesgas.ui.theme.FontMed
import com.cesoft.cesgas.ui.theme.SepMax
import com.cesoft.cesgas.ui.theme.SepMed
import com.cesoft.cesgas.ui.theme.SepMin

data class UniqueFilterField(
    val id: Int,
    val name: String,
    val selected: Boolean = false,
) {
    companion object {
        val Empty = UniqueFilterField(0, "")
    }
}

@Immutable
data class UniqueFilter(
    val fields: List<UniqueFilterField>
) {
    fun select(id: Int, selected: Boolean): UniqueFilter {
        val list = fields.toMutableList()
        for(i in 0..< list.size) {
            if(list[i].id == id) {
                list[i] = list[i].copy(selected = selected)
            }
            else {
                list[i] = list[i].copy(selected = false)
            }
        }
        return UniqueFilter(list.toList())
    }
    fun getSelected(): UniqueFilter {
        return UniqueFilter(fields.filter { it.selected })
    }
}

@Composable
fun UniqueFilterCompo(
    title: String,
    isVisible: MutableState<Boolean>,
    filter: UniqueFilter,
    onSave: (UniqueFilter) -> Unit,
) {
    val remFilter = remember { mutableStateOf(filter) }
    if(isVisible.value) {
        Surface(shadowElevation = SepMin) {
            UniqueFilterList(
                title = title,
                filter = remFilter.value,
                onClose = {
                    remFilter.value = filter
                    isVisible.value = false
                },
                onSave = {
                    onSave(remFilter.value)
                    isVisible.value = false
                },
                onClick = { id, selected ->
                    remFilter.value = remFilter.value.select(id, selected)
                }
            )
        }
    }
    else {
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            item {
                AddItemButton(title) { isVisible.value = true }
            }
            for(field in remFilter.value.getSelected().fields) {
                item {
                    SpendableCompo(
                        text = field.name,
                        onClose = {
                            remFilter.value = remFilter.value.select(field.id, false)
                            onSave(remFilter.value)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun UniqueFilterList(
    title: String,
    filter: UniqueFilter,
    onClose: () -> Unit,
    onSave: () -> Unit,
    onClick: (Int, Boolean) -> Unit,
) {
    val loc = remember { mutableStateOf(filter) }
    Column {
        Row(Modifier.padding(top = SepMax*2, start = SepMax, bottom = SepMax*2)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.clickable { onClose() }
            )
            Text(
                text = stringResource(R.string.select) + " $title",
                fontWeight = FontWeight.Bold,
                fontSize = FontMed,
                modifier = Modifier.padding(start = SepMax*3)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(.9f)
        ) {
            loc.value.fields.forEach { (id, name, selected) ->
                item {
                    Item(id, name, selected) { id, value ->
                        //loc.value = Filters(listOf())//Forces a recomposition
                        loc.value = loc.value.select(id, value)
                        onClick(id, value)
                    }
                }
            }
        }
        SaveCloseButtons(
            onSave = onSave,
            modifier = Modifier
                .weight(.1f)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun Item(
    id: Int,
    name: String,
    selected: Boolean,
    onClick: (Int, Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SepMax)
            .height(35.dp)
    ) {
        Text(
            text = AnnotatedString(name),
            modifier = Modifier
                .weight(1f)
                .align(CenterVertically)
                .clickable { onClick(id, !selected)}
        )
        RadioButton(
            onClick = { onClick(id, !selected) },
            selected = selected,
        )
    }
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .width(1.dp)
            .padding(start = SepMax, top = SepMed, bottom = SepMed)
    )
}

//--------------------------------------------------------------------------------------------------
@Composable
private fun AddItemButton(
    title: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        contentPadding = PaddingValues(SepMax, 0.dp),
        shape = CylinderShape,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = SepMin),
        content = { Text(title) }
    )
}

//--------------------------------------------------------------------------------------------------
@Composable
private fun SaveCloseButtons(
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        thickness = 2.dp,
        modifier = Modifier.padding(SepMed)
    )
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.padding(top = SepMin)
    ) {
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(.8f)
        ) {
            Text(text = stringResource(R.string.apply))
        }
    }
}

//--------------------------------------------------------------------------------------------------
private class UPreviewZoneUiValue(
    val isVisible: Boolean,
    val filter: UniqueFilter,
)
private class UZoneUiProvider: PreviewParameterProvider<UPreviewZoneUiValue> {
    val filter = UniqueFilter(listOf(
        UniqueFilterField(5, "Item A5", false),
        UniqueFilterField(7, "Item B7", true),
        UniqueFilterField(11, "Item B11", true),
        UniqueFilterField(15, "Item B15", false),
    ))
    override val values = sequenceOf(
        UPreviewZoneUiValue(isVisible = false, filter = filter),
        UPreviewZoneUiValue(isVisible = false, filter = filter),
        UPreviewZoneUiValue(isVisible = true, filter = filter),
    )
}
@Preview
@Composable
private fun Zone_Preview(@PreviewParameter(UZoneUiProvider::class) value: UPreviewZoneUiValue) {
    val isVisible = remember { mutableStateOf(value.isVisible) }
    val filter = value.filter
    Surface(modifier = Modifier.fillMaxWidth()) {
        UniqueFilterCompo(
            title = "Campo",
            isVisible = isVisible,
            filter = filter,
            onSave = {},
        )
    }
}
