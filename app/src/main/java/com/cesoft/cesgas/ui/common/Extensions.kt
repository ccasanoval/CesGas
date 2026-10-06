package com.cesoft.cesgas.ui.common

import androidx.compose.ui.text.intl.PlatformLocale

fun Float?.toMoneyFormat(locale: PlatformLocale) : String {
    if(this == null) return "— €"
    return String.format(locale,"%,.3f €",this)
}
