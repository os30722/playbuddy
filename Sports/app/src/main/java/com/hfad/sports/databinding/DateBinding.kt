package com.hfad.sports.databinding

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.hfad.sports.util.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*

object DateBinding {
    private val dateFormat = SimpleDateFormat(DateTimeFormat.AppDateFormat, Locale.US)

    @JvmStatic
    @InverseBindingAdapter(attribute = "datePick")
    fun getDatePick(view: EditText): String {
        return view.text.toString()
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "eventDatePick")
    fun getEventDatePick(view: EditText): String {
        return view.text.toString()
    }

    @JvmStatic
    @BindingAdapter(value = ["datePickAttrChanged"])
    fun setDateListener(view: EditText, listener: InverseBindingListener?) {
        view.setOnClickListener {
            val dialog =  showDatePickerDialog(view, listener)
            dialog.show()
        }
    }


    @JvmStatic
    @BindingAdapter(value = ["datePick"])
    fun setDatePick(view: EditText, date: String?) {
        view.setText(date)
    }

    @JvmStatic
    @BindingAdapter(value = ["eventDatePick"])
    fun setEventDatePick(view: EditText, date: String?) {
        view.setText(date)
    }


    @JvmStatic
    @BindingAdapter(value = ["eventDatePickAttrChanged"])
    fun setEventDateListener(view: EditText, listener: InverseBindingListener?){
        view.setOnClickListener {
            val dialog =  showDatePickerDialog(view, listener)
            setDateConstraints(dialog)
            dialog.show()
        }
    }


    // Function work for Android 5.0 (Lollipop)
    private fun setDateConstraints(dialog: DatePickerDialog){
        val calendar = Calendar.getInstance()
        dialog.datePicker.apply {
            minDate = calendar.timeInMillis
            calendar.add(Calendar.MONTH, 1)
            maxDate = calendar.timeInMillis
        }

    }

    private fun showDatePickerDialog(view: EditText, listener: InverseBindingListener?):
            DatePickerDialog {
        val calendar = Calendar.getInstance()
        return DatePickerDialog(
            view.context,
            { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val date = Calendar.getInstance()
                date[year, month] = dayOfMonth
                view.setText(dateFormat.format(date.time))
                listener?.onChange()
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )

    }

    @JvmStatic
    @BindingAdapter("date")
    fun setDate(view: TextView, date: String){
        view.setText(DateTimeFormat.appDateFormat(DateTimeFormat.ServerDateFormat, date))
    }


}