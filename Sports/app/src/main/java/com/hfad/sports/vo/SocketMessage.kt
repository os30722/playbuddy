package com.hfad.sports.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class SocketMessage(
     @field:SerializedName("event") val event: String,
     @field:SerializedName("data") val data: JsonObject
)

data class SendEvent(
     @field:SerializedName("event") val event: String,
     @field:SerializedName("data") val data: Any
)



