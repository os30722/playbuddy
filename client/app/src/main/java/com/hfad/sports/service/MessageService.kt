package com.hfad.sports.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.hfad.sports.vo.SendEvent
import kotlinx.coroutines.*
import kotlin.reflect.KClass


data class Handler<T: Any>(val type: KClass<T>?, val call: (T) -> Unit )

class MessageService : Service(), SocketListener {

    private val binder = MessageBinder()
    private lateinit var socket: MessageSocket

    private var listener: MutableMap<MsgEvent, Handler<Any>> = mutableMapOf()

    override fun onCreate() {
        Log.d("debug64", "Service created")
        socket = MessageSocket()
        socket.setOnSocketListener(this)
        super.onCreate()
    }

    inner class MessageBinder() : Binder() {
        fun getService(): MessageService  = this@MessageService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        Log.d("debug64", "Service end")
        socket.clear()
        super.onDestroy()
    }


    override fun onSocketEvent(event: MsgEvent, data: String?) {
        val handler = listener[event]
        if(handler != null){
            handler.call(Gson().fromJson(data, handler.type?.java))
        } else {
            Log.d("debug64","Error => Handler not Found")
        }
    }

    fun <T: Any> onListen(event: MsgEvent, type: KClass<T>, call: (T) -> Unit ){
        listener[event] = Handler(type, call) as Handler<Any>
    }

    fun sendMessage(event: MsgEvent, data: Any){
        val payload = SendEvent(event = event.event, data)
            socket.webSocket?.send(Gson().toJson(payload))

    }


    fun removeListeners(){
        listener.clear()
    }

}




