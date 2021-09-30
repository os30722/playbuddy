package com.hfad.sports.vo

import android.os.Parcelable
import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Event (
        @field:SerializedName("sport") val Sport: String,
        @field:SerializedName("event_date") val eventDate: String,
        @field:SerializedName("start_time") val startTime: String,
        @field:SerializedName("end_time") val endTime: String,
        @field:SerializedName("instruction") val instruction: String,
        @field:SerializedName("location") val location: LatLng,
        @field:SerializedName("address") val address: String,
        @field:SerializedName("no_players") val noPlayers: Int
)


@Parcelize
@Entity(tableName = "event")
data class EventPage (
        @PrimaryKey
        @field:SerializedName("event_id")val eventId: Int,
        @field:SerializedName("sport")val sport: String,
        @Embedded
        @field:SerializedName("host") val host: UserPage,
        @field:SerializedName("event_date") val eventDate: String,
        @field:SerializedName("start_time") val startTime: String,
        @field:SerializedName("end_time") val endTime: String,
        @field:SerializedName("address") val address: String,
        @field:SerializedName("total_player") val totalPlayer: Int,
        @field:SerializedName("joined") val joined: Int,
        @field:SerializedName("requests") val requests: Int,
        @field:SerializedName("instruction") val instruction: String?,
        @field:SerializedName("player_joined") val playerJoined: JoinStatus?


): Parcelable

data class EventList(
        @field:SerializedName("total_count") val totalCount: Int,
        @field:SerializedName("items") val items: List<EventPage>
)

@Entity(tableName = "event_keys")
data class EventKeys(
        @PrimaryKey val eventId: Int,
        val nextKey: Int?
)

enum class JoinStatus(val status: String) {
        @SerializedName("jo")
        JOINED("jo"),
        @SerializedName("nj")
        NOT_JOINED("nj"),
        @SerializedName("request")
        REQUEST("request"),
        @SerializedName("host")
        HOST("host");

        companion object {
                fun getEnum(status: String?): JoinStatus? {
                        return when (status) {
                                "nj" -> NOT_JOINED
                                "jo" -> JOINED
                                "request" -> REQUEST
                                "host" -> HOST
                                else -> null
                        }
                }
        }
}


