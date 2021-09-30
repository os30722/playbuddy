package com.hfad.sports.vo

import com.google.gson.annotations.SerializedName

data class ResponseHolder(
    @field:SerializedName("msg") val message: String,
    @field:SerializedName("error") val error: String
)
