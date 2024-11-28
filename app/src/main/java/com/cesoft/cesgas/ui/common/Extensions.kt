package com.cesoft.cesgas.ui.common

import androidx.compose.ui.text.intl.PlatformLocale

fun Float?.toMoneyFormat(locale: PlatformLocale) : String {
    if(this == null) return "0 €"
    return String.format(locale,"%,.2f €",this)
}
