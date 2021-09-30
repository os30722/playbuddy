package com.hfad.sports.util

import java.text.SimpleDateFormat
import java.util.*

class   DateTimeFormat {
    companion object {
        const val AppTimeFormat = "hh : mm aa"
        const val ServerTimeFormat = "HH:mm"
        const val ServerDateFormat = "yyyy-MM-dd"
        const val AppDateFormat = "dd MMMM yyyy"

        fun timeFormatUTC(format: String, str: String): String{
            val localFormat = SimpleDateFormat(format, Locale.getDefault())
            val localTime = localFormat.parse(str);

            val utcFormat = SimpleDateFormat(ServerTimeFormat, Locale.UK)
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            return if(localTime != null ) utcFormat.format(localTime).toString() else ""
        }

        @JvmStatic
        fun timeFormatLocal(format: String, str: String): String {
            val utcTimeFormat = SimpleDateFormat(format, Locale.UK)
            utcTimeFormat.timeZone = TimeZone.getTimeZone("UTC")
            val utcTime = utcTimeFormat.parse(str)

            val localtimeFormat = SimpleDateFormat(AppTimeFormat, Locale.getDefault())
            localtimeFormat.timeZone = TimeZone.getDefault()
            return if (utcTime != null) localtimeFormat.format(utcTime).toString() else ""
        }

        fun appTimeFormat(format: String, str: String ) : String{
            val time = SimpleDateFormat(format, Locale.ENGLISH).parse(str)
            val timeFormatter = SimpleDateFormat(AppTimeFormat, Locale.getDefault())
            return if(time != null) timeFormatter.format(time).toString() else ""
        }

        fun appDateFormat(format: String, str: String): String{
            val date = SimpleDateFormat(format, Locale.ENGLISH).parse(str)
            val dateFormatter = SimpleDateFormat(AppDateFormat, Locale.getDefault())
            return if(date != null) dateFormatter.format(date).toString() else ""
        }


        fun dateServerFormat(format: String, str: String) : String {
            val dateFormat = SimpleDateFormat(format , Locale.getDefault())
            val dateString = dateFormat.parse(str)

            val serverFormat = SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH)
            return if (dateString != null) serverFormat.format(dateString).toString() else ""
        }

    }
}