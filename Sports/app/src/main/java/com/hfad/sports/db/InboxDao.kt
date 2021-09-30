package com.hfad.sports.db

import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import androidx.room.*
import com.hfad.sports.api.UserApi
import com.hfad.sports.util.TokenToolkit
import com.hfad.sports.vo.Conversation
import com.hfad.sports.vo.EventPage
import com.hfad.sports.vo.Inbox
import com.hfad.sports.vo.UserPage
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@Dao
interface InboxDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(inbox: List<Inbox>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addInbox(inbox: Inbox)

    @Query("select * from inbox order by id desc")
    fun pagingSource(): PagingSource<Int, Inbox>

    @Query("delete from inbox")
    suspend fun clearAll()

    @Update(entity = Inbox::class)
    suspend fun update(inbox: InboxUpdate): Int

    @Query("update inbox set isRead = 1 where senderId = :senderId")
    suspend fun readAll(senderId: Int)

    suspend fun updateInbox(conversation: Conversation){
        val userId =
            if (TokenToolkit.getUid() == conversation.senderId) conversation.recipientId else conversation.senderId
        val inboxUpdate = InboxUpdate(
            userId,
            false,
            conversation.id,
            conversation.senderId,
            conversation.message
        )
        val res = update(inboxUpdate)
        if (res == 0){
            try {
                val body = UserApi.create().getUserPage(userId)
                val inbox = Inbox(body, conversation.id, conversation.senderId, conversation.isRead, conversation.message )
                addInbox(inbox)

            } catch (e: IOException){
                Log.d("debug64","Update Error")
            } catch (e: HttpException){
                Log.d("debug64","Update Error")
            }
            }
        }


}

@Entity
data class InboxUpdate(
    @PrimaryKey
    val userId: Int,
    val isRead: Boolean,
    val id: Int,
    val senderId: Int,
    val message: String
)