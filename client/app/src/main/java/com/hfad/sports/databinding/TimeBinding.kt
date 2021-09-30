package com.hfad.sports.databinding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.hfad.sports.util.DateTimeFormat

object TimeBinding {

    @JvmStatic
    @BindingAdapter("time")
    fun setTime(view: TextView, time: String){
        view.setText(DateTimeFormat.timeFormatLocal("HH:mm", time))
    }

}