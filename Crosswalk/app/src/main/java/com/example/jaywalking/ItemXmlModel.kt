package com.example.jaywalking

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "TbWtrmscrslkTfcacdarM")
data class TbWtrmscrslkTfcacdarMInfo(
    @Element
    val head : Head,
    @Element(name = "row")
    val row : MutableList<myRow>

)

@Xml(name = "head")
data class Head(
    @PropertyElement
    val list_total_count : Int,
    @Element
    val RESULT : result,
    @PropertyElement
    val api_version : String
)

@Xml(name = "RESULT")
data class result(
    @PropertyElement
    val CODE : String,
    @PropertyElement
    val MESSAGE : String
)

@Xml
data class myRow(
    @PropertyElement
    val JURISD_POLCSTTN_NM : String?,
    @PropertyElement
    val LOC_INFO : String?,
    @PropertyElement
    val OCCUR_CNT : String?
){
    constructor() : this(null, null, null)
}