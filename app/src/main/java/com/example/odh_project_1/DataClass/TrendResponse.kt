package com.example.odh_project_1.DataClass
import com.google.gson.annotations.SerializedName

data class TrendResponse(
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("timeUnit") val timeUnit: String,
    @SerializedName("results") val results: List<ResultData>
) {
    data class ResultData(
        @SerializedName("title") val title: String,
        @SerializedName("keywords") val keywords: List<String>,
        @SerializedName("data") val data: List<PeriodData>
    ) {
        data class PeriodData(
            @SerializedName("period") val period: String,
            @SerializedName("ratio") val ratio: Float
        )
    }
}