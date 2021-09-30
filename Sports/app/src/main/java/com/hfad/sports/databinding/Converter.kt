package com.hfad.sports.databinding

import androidx.room.TypeConverter
import com.hfad.sports.vo.JoinStatus

class Converters {

    @TypeConverter
    fun toJoinStatus(value: String?) = JoinStatus.getEnum(value)

    @TypeConverter
    fun fromJoinStatus(value: JoinStatus?) = value?.status
}