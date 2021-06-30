package ru.androidlearning.theuniversearoundus.model.web.data_sources.api

import com.google.gson.annotations.SerializedName

data class PictureOfTheDayDTO(
    @field:SerializedName("date") val date: String?,
    @field:SerializedName("explanation") val explanation: String?,
    @field:SerializedName("media_type") val mediaType: String?,
    @field:SerializedName("title") val title: String?,
    @field:SerializedName("url") val url: String?,
    @field:SerializedName("copyright") val copyright: String?,
    @field:SerializedName("hdurl") val hdUrl: String?
)
