package com.example.odh_project_1.DataClass
import com.google.gson.annotations.SerializedName

data class TrendRequestBody(
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("timeUnit") val timeUnit: String,
    @SerializedName("keywordGroups") val keywordGroups: List<KeywordGroup>
) {
    data class KeywordGroup(
        @SerializedName("groupName") val groupName: String,
        @SerializedName("keywords") val keywords: List<String>
    )
}