package com.hfad.sports.vo

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class UserLogin(
    @field:SerializedName("email") val email: String,
    @field:SerializedName("pass") val pass: String
)

data class UserSignUp(
    @field:SerializedName("firstname") val firstName: String,
    @field:SerializedName("lastname") val lastName: String,
    @field:SerializedName("dob") val dateOfBirth: String,
    @field:SerializedName("gender") val gender: String,
    @field:SerializedName("email") val email: String,
    @field:SerializedName("pass") val pass: String
)

data class Token(
    @field:SerializedName("uid") val userId: Int?,
    @field:SerializedName("refresh_token") val refreshToken: String,
    @field:SerializedName("access_token") val accessToken: String
)

@Parcelize
data class UserPage(
    @PrimaryKey
    @field:SerializedName("uid") val userId: Int,
    @field:SerializedName("profile_pic") val profilePic: String,
    @field:SerializedName("firstname") val firstName: String,
    @field:SerializedName("lastname") val lastName: String
) : Parcelable

data class UserProfile(
    @field:SerializedName("user") val user: UserPage,
    @field:SerializedName("friend_status") val friendStatus: String?,
    @field:SerializedName("no_events") val noEvents: Int,
    @field:SerializedName("friends") val friends: Int,
    @field:SerializedName("reputation") val reputation: Int
)

data class UserList(
    @field:SerializedName("total_count") val totalCount: Int,
    @field:SerializedName("items") val items: List<UserPage>
)

data class LoggedUser(
    @field:SerializedName("device_id") val deviceId: String
)
