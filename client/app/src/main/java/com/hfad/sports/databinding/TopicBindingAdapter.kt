package com.hfad.sports.databinding

import android.graphics.Color
import android.graphics.Typeface
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.annotations.SerializedName
import com.hfad.sports.api.AuthApi
import com.hfad.sports.util.TokenToolkit
import com.hfad.sports.vo.Inbox
import com.hfad.sports.vo.JoinStatus


object TopicBindingAdapter {


    @JvmStatic
    @BindingAdapter("android:text")
    fun setText(view: TextInputEditText, str: String?) {
        if (str != null) {
            view.setText(str)
            view.setSelection(str.length)
        }
    }

    // To display image (Profile pic)
    @JvmStatic
    @BindingAdapter("imgSrc")
    fun setImage(v: ShapeableImageView, src: String?) {
        if (src != null) {
            Glide.with(v.context)
                .load(AuthApi.BASE_URL + src)
                .apply(RequestOptions().centerCrop().format(DecodeFormat.PREFER_ARGB_8888))
                .into(v)
        }
    }

    // To show feedback fro friend request
    @JvmStatic
    @BindingAdapter("friends")
    fun setFriends(v: MaterialButton, status: String?) {
        if (status != null) {
            when (status) {
                "request" -> v.text = "Request Sent"
                "confirm" -> v.text = "Friends"

            }
        }
    }


    // To show feedback for player joining
    @JvmStatic
    @BindingAdapter("joined")
    fun setJoined(v: MaterialButton, status: JoinStatus?) {
        when (status) {
            JoinStatus.NOT_JOINED -> v.setText("Join Game")
            JoinStatus.JOINED -> v.setText("Leave Game")
            JoinStatus.REQUEST -> v.setText("Request Sent")
        }
    }

    // To show feedback for unseen messages
    @JvmStatic
    @BindingAdapter("seen")
    fun setRead(t: TextView, inbox: Inbox) {
        if (!inbox.isRead && TokenToolkit.getUid() != inbox.senderId) {
            t.setTextColor(Color.BLACK)
            t.setTypeface(null, Typeface.BOLD)
        }
    }
}

