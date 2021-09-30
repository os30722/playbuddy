package com.hfad.sports.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfad.sports.vo.EventKeys
import com.hfad.sports.vo.InboxKeys

@Dao
interface InboxKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: InboxKeys)

    @Query("select * from inbox_keys where uid = :uid")
    suspend fun eventKeysId(uid: Int): InboxKeys?

    @Query("delete from inbox_keys")
    suspend fun clearInboxKeys()

    @Query("select nextKey from inbox_keys limit 1")
    suspend fun getNextKey():Int

}