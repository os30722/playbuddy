package com.hfad.sports.service

import android.util.Log
import com.google.gson.Gson
import com.hfad.sports.API_BASE_URL
import com.hfad.sports.util.TokenToolkit
import com.hfad.sports.vo.SocketMessage
import okhttp3.*
import okio.ByteString

interface SocketListener {
    fun onSocketEvent(event: MsgEvent, data: String?)
}

enum class MsgEvent(val event: String) {
    CHAT_MESSAGE("chat_message"),
    SEEN_MESSAGE("seen_message");

    companion object  {
        fun getValue(event: String): MsgEvent?{
            return when(event){
                "chat_message" -> CHAT_MESSAGE
                "seen_message" -> SEEN_MESSAGE
                else -> null
            }
        }
    }
}

class MessageSocket : WebSocketListener() {

    var webSocket: WebSocket?
    private var client: OkHttpClient

    private var callback: SocketListener? = null

    fun setOnSocketListener(callback: SocketListener){
        this.callback = callback

    }

    companion object {
        const val SOCKET_URL = API_BASE_URL + "/msg/ws"
        const val NORMAL_CLOSE_STATUS = 1000
    }

    init {
        val request =
            Request.Builder().addHeader("Authorization", "Bearer " + TokenToolkit.getAccessToken())
                .url(SOCKET_URL).build()
        client = OkHttpClient()
        webSocket = client.newWebSocket(request, this)

        client.connectionPool().evictAll()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("debug64", "Websocket opened")

    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("debug64", "Received bytes := " + bytes.hex())
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("debug64", "Received string := " + text)
        val gson = Gson()
        val msg: SocketMessage = gson.fromJson(text, SocketMessage::class.java)
        var event: MsgEvent? = MsgEvent.getValue(msg.event)

        if (event != null) {
            callback?.onSocketEvent(event, msg.data.toString())
        }

    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("debug64", "Closed socket")
        clear()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("debug64", "Error := " + t.message.toString())
    }

    fun clear(){
        webSocket?.close(NORMAL_CLOSE_STATUS, null)
        webSocket = null
        client.connectionPool().evictAll()

    }


}

