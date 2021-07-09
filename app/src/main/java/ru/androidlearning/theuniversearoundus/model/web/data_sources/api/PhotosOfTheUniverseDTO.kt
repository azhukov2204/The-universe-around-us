package ru.androidlearning.theuniversearoundus.model.web.data_sources.api

import com.google.gson.annotations.SerializedName

data class PhotosOfTheUniverseDTO(
    @field:SerializedName("collection") val collection: Collection
)

data class Collection(
    @field:SerializedName("links") val links: List<Links>,
    @field:SerializedName("items") val items: List<Items>,
    @field:SerializedName("version") val version: Double,
    @field:SerializedName("metadata") val metadata: Metadata,
    @field:SerializedName("href") val href: String
)

data class Data(
    @field:SerializedName("title") val title: String,
    @field:SerializedName("center") val center: String,
    @field:SerializedName("date_created") val date_created: String,
    @field:SerializedName("description") val description: String,
    @field:SerializedName("keywordswords") val keywordswords: List<String>,
    @field:SerializedName("media_type") val media_type: String,
    @field:SerializedName("nasa_id") val nasa_id: String
)

data class Items(
    @field:SerializedName("links") val links: List<Links>,
    @field:SerializedName("data") val data: List<Data>,
    @field:SerializedName("href") val href: String
)

data class Links(
    @field:SerializedName("rel") val rel: String,
    @field:SerializedName("render") val render: String,
    @field:SerializedName("href") val href: String
)

data class Metadata(
    @field:SerializedName("total_hits") val total_hits: Int
)
