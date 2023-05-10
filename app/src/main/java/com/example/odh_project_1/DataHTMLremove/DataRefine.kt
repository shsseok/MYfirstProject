package com.example.odh_project_1

fun String.removeHtmlTags(): String {
        val htmlTagRegex = Regex("<.*?>")
        return this.replace(htmlTagRegex, "")
}
