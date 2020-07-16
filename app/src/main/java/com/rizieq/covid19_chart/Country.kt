package com.rizieq.covid19_chart

data class AllNegara (
    val Global: Dunia,
    val Countries: List<Negara>
)

data class Dunia(
    val TotalConfirmed: String = "",
    val TotalRecovered : String = "",
    val TotalDeaths : String = ""
)


data class Negara(
    val Country : String = "",
    val CountryCode : String = "",
    val Slug : String = "",
    val NewConfirmed :String = "",
    val TotalConfirmed : String = "",
    val NewDeaths: String = "",
    val TotalDeaths : String = "",
    val NewRecovered: String = "",
    val TotalRecovered : String = "",
    val Date : String = ""
)

data class InfoNegara(
    val Confirmed : String = "",
    val Deaths : String = "",
    val Recovered : String = "",
    val Active : String = "",
    val Date : String = ""

)



