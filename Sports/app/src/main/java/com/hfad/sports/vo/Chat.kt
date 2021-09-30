package com.hfad.sports.vo

import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class SendMessage (
    @field:SerializedName("recv_uid") val recvUid: Int,
    @field:SerializedName("msg") val message: String,
)

data class ConversationPage(
    @field:SerializedName("total_count") val totalCount: Int,
    @field:SerializedName("items") val items: List<Conversation>
)

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("sender_uid") val senderId: Int,
    @field:SerializedName("recipient_uid") val recipientId: Int,
    @field:SerializedName("is_read") val isRead: Boolean,
    @field:SerializedName("msg") val message: String,
    @field:SerializedName("timestamp") val timeStamp: String

)

@Entity(tableName = "inbox", primaryKeys = ["userId"])
data class Inbox(
    @Embedded
    @field:SerializedName("user") val user: UserPage,
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("sender_uid") val senderId: Int,
    @field:SerializedName("is_read") val isRead: Boolean,
    @field:SerializedName("msg") val message: String
)


data class InboxPage(
    @field:SerializedName("total_count") val totalCount: Int,
    @field:SerializedName("items") val items: List<Inbox>
)

@Entity(tableName = "inbox_keys")
data class InboxKeys(
    @PrimaryKey
    val uid: Int,
    val nextKey: Int?
)