package com.cesoft.domain.entity

enum class ProductType { ALL, UNKNOWN, G95, G98, GOA, GOB, GOC, GOAP, GLP,}
data class Product(
    val id: Int,
    val name: String,
    val acronym: String,
) {
    val type: ProductType
        get() = if(isG95) ProductType.G95
                else if(isG98) ProductType.G98
                else if(isGOA) ProductType.GOA
                else if(isGOB) ProductType.GOB
                else if(isGOC) ProductType.GOC
                else if(isGOAP) ProductType.GOAP
                else if(isGLP) ProductType.GLP
                else ProductType.UNKNOWN
    //fun is95() = acronym.contains("95")
    val isG95: Boolean
        get() = acronym.contains("G95")
    val isG98: Boolean
        get() = acronym.contains("G98")
    val isGOA: Boolean
        get() = acronym == "GOA"
    val isGOAP: Boolean
        get() = acronym == "GOA+"
    val isGOB: Boolean
        get() = acronym == "GOB"
    val isGOC: Boolean
        get() = acronym == "GOC"
    val isGLP: Boolean
        get() = acronym == "GLP"

    val simpleName: String
        get() = if(isG95) "Gasolina 95"
                else if(isG98) "Gasolina 98"
                else if(isGOA) "Gasóleo A"
                else if(isGOB) "Gasóleo B"
                else if(isGOC) "Gasóleo C"
                else if(isGOAP) "Gasóleo Premium"
                else if(isGLP) "GLP"
                //TODO: keep working...
                else "Otros"
}
// {"IDProducto":"1","NombreProducto":"Gasolina 95 E5","NombreProductoAbreviatura":"G95E5"},
// {"IDProducto":"23","NombreProducto":"Gasolina 95 E10","NombreProductoAbreviatura":"G95E10"},
// {"IDProducto":"20","NombreProducto":"Gasolina 95 E5 Premium","NombreProductoAbreviatura":"G95E5+"},
// {"IDProducto":"3","NombreProducto":"Gasolina 98 E5","NombreProductoAbreviatura":"G98E5"},
// {"IDProducto":"21","NombreProducto":"Gasolina 98 E10","NombreProductoAbreviatura":"G98E10"},
// {"IDProducto":"4","NombreProducto":"Gasóleo A habitual","NombreProductoAbreviatura":"GOA"},
// {"IDProducto":"5","NombreProducto":"Gasóleo Premium","NombreProductoAbreviatura":"GOA+"},
// {"IDProducto":"6","NombreProducto":"Gasóleo B","NombreProductoAbreviatura":"GOB"},
// {"IDProducto":"7","NombreProducto":"Gasóleo C","NombreProductoAbreviatura":"GOC"},
// {"IDProducto":"16","NombreProducto":"Bioetanol","NombreProductoAbreviatura":"BIE"},
// {"IDProducto":"8","NombreProducto":"Biodiésel","NombreProductoAbreviatura":"BIO"},
// {"IDProducto":"17","NombreProducto":"Gases licuados del petróleo","NombreProductoAbreviatura":"GLP"},
// {"IDProducto":"18","NombreProducto":"Gas natural comprimido","NombreProductoAbreviatura":"GNC"},
// {"IDProducto":"19","NombreProducto":"Gas natural licuado","NombreProductoAbreviatura":"GNL"},
// {"IDProducto":"22","NombreProducto":"Hidrógeno","NombreProductoAbreviatura":"H2"},
// {"IDProducto":"9","NombreProducto":"Fuelóleo bajo índice azufre","NombreProductoAbreviatura":"FOB"},
// {"IDProducto":"10","NombreProducto":"Fuelóleo especial","NombreProductoAbreviatura":"FOE"},
// {"IDProducto":"11","NombreProducto":"Gasóleo para uso marítimo","NombreProductoAbreviatura":"MGO"},
// {"IDProducto":"12","NombreProducto":"Gasolina de aviación","NombreProductoAbreviatura":"GNAV"},
// {"IDProducto":"13","NombreProducto":"Queroseno de aviación JET_A1","NombreProductoAbreviatura":"JETA1"},
// {"IDProducto":"14","NombreProducto":"Queroseno de aviación JET_A2","NombreProductoAbreviatura":"JETA2"}]