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
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
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
import com.cesoft.cesgas.ui.theme.Yellow

data class FilterField(
    val id: Int,
    val name: String,
    val selected: Boolean = false,
    val favorite: Boolean = false,
)

@Immutable
data class FilterOptions(
    val fields: List<FilterField>
) {
    fun favorite(id: Int, favorite: Boolean): FilterOptions {
        val list = fields.toMutableList()
        for(i in 0..< list.size) {
            if(list[i].id == id) {
                list[i] = list[i].copy(favorite = favorite)
            }
        }
        return FilterOptions(list.toList())
    }
    fun select(id: Int, selected: Boolean, unique: Boolean): FilterOptions {
        val list = fields.toMutableList()
        for(i in 0..< list.size) {
            if(unique) list[i] = list[i].copy(selected = false)
            if(list[i].id == id) {
                list[i] = list[i].copy(selected = selected)
            }
        }
        return FilterOptions(list.toList())
    }
    fun getSelected(): FilterOptions {
        return FilterOptions(fields.filter { it.selected })
    }
    fun getSelectedId(): Int? {
        return fields.firstOrNull { it.selected }?.id
    }
}

@Composable
fun FilterCompo(
    title: String,
    isVisible: MutableState<Boolean>,
    filter: FilterOptions,
    unique: Boolean,
    onSave: (FilterOptions) -> Unit,
) {
    val remFilter = remember { mutableStateOf(filter) }
    if(isVisible.value) {
        Surface(shadowElevation = SepMin) {
            FilterList(
                title = title,
                filter = remFilter.value,
                unique = unique,
                onClose = {
                    remFilter.value = filter
                    isVisible.value = false
                },
                onSave = {
                    onSave(remFilter.value)
                    isVisible.value = false
                },
                onFavorite = { id, favorite ->
                    remFilter.value = remFilter.value.favorite(id, favorite)
                },
                onClick = { id, selected ->
                    remFilter.value = remFilter.value.select(id, selected, unique)
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
                            remFilter.value = remFilter.value.select(field.id, false, unique)
                            onSave(remFilter.value)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterList(
    title: String,
    filter: FilterOptions,
    unique: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit,
    onFavorite: (Int, Boolean) -> Unit,
    onClick: (Int, Boolean) -> Unit,
) {
    val loc = remember { mutableStateOf(filter) }
    Column {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SepMax*2, start = SepMax, bottom = SepMax*2)
        ) {
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
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(.2f)
            ) {
                Button(
                    onClick = onSave,
                    modifier = Modifier.padding(horizontal = SepMed)
                ) { Text(text = stringResource(R.string.apply)) }
            }
        }
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(.9f)) {
            loc.value.fields.sortedBy { !it.favorite } .forEach { (id, name, selected, favorite     ) ->
                item {
                    Item(
                        id = id,
                        name = name,
                        selected = selected,
                        favorite = favorite,
                        onFavorite = { id, value ->
                            loc.value = loc.value.favorite(id, value)
                            onFavorite(id, value)
                        }
                    ) { id, value ->
                        //loc.value = Filters(listOf())//Forces a recomposition
                        loc.value = loc.value.select(id, value, unique)
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
    favorite: Boolean,
    onFavorite: (Int, Boolean) -> Unit,
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
        val icon = if(favorite) Icons.Filled.Star else Icons.Outlined.Star
        val iconTintColor = if (favorite) Yellow else MaterialTheme.colorScheme.surfaceDim
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTintColor,
            modifier = Modifier
                .selectable(
                    selected = favorite,
                    onClick = { onFavorite(id, !favorite) }
                )
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
private class PreviewZoneUiValue(
    val isVisible: Boolean,
    val filter: FilterOptions,
)
private class ZoneUiProvider: PreviewParameterProvider<PreviewZoneUiValue> {
    val filter = FilterOptions(listOf(
        FilterField(5, "Item A-5", false, false),
        FilterField(7, "Item B-7", true, false),
        FilterField(11, "Item C-11", true, true),
        FilterField(15, "Item D-15", false, false),
    ))
    override val values = sequenceOf(
        PreviewZoneUiValue(isVisible = false, filter = filter),
        PreviewZoneUiValue(isVisible = false, filter = filter),
        PreviewZoneUiValue(isVisible = true, filter = filter),
    )
}
@Preview
@Composable
private fun Zone_Preview(@PreviewParameter(ZoneUiProvider::class) value: PreviewZoneUiValue) {
    val isVisible = remember { mutableStateOf(value.isVisible) }
    val filter = value.filter
    Surface(modifier = Modifier.fillMaxWidth()) {
        FilterCompo(
            title = "Campo",
            isVisible = isVisible,
            filter = filter,
            unique = false,
            onSave = {},
        )
    }
}
