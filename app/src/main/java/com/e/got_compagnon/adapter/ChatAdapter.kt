package com.e.got_compagnon.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.e.got_compagnon.R
import com.e.got_compagnon.model.Chat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class ChatAdapter(var chatList: List<Chat>): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    //Inflate view (xml layout) -> ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val chatView = LayoutInflater.from(parent.context).inflate(R.layout.my_message, parent, false)
        return ChatViewHolder(chatView)
    }

    //Update view for position
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.messageTextView.text = chat.message
        //holder.usernameTextView.text = chat.username
        //TODO: Format Date
        //holder.dateTextView.text = chat.sentAt.toString()
        val finalTime = chat.sentAt?.let { convertLongToTime(it.toLong()) }
        holder.dateTextView.text = finalTime
    }

    //Total items
    override fun getItemCount(): Int {
        return chatList.count()
    }

    //Maps view xml -> Kotlin
    inner class ChatViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.messageBody)
        //val usernameTextView: TextView = view.findViewById(R.id.messageUsernameTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateText)
    }

    fun convertLongToTime(time: Long): String{
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm dd.MM.yyyy")
        return format.format(date)
    }
}