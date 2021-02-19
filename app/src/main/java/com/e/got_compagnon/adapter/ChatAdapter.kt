package com.e.got_compagnon.adapter

import android.annotation.SuppressLint
import android.app.ActionBar
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.got_compagnon.Constants
import com.e.got_compagnon.R
import com.e.got_compagnon.model.Chat
import com.e.got_compagnon.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class ChatAdapter(var chatList: List<Chat>): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    //Inflate view (xml layout) -> ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val chatView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(chatView)
    }

    //Update view for position
    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        val uid = Firebase.auth.uid

        val chat = chatList[position]
        if(chat.userId == uid) {
            holder.rootLayout.apply {
                val lPram = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT, Gravity.END)
                layoutParams = lPram
            }

            holder.messageAvatar.visibility = View.GONE
            holder.messageUsername.visibility = View.GONE

            holder.messageTextView.text = chat.message
            holder.messageTextView.apply {
                setBackgroundResource(R.drawable.my_message)
            }
            /*
            val finalTime = chat.sentAt?.let { convertLongToTime(it.toLong()) }
            holder.messageDateTextView.text = finalTime
             */

            holder.messageDateTextView.text = convertTimestampToTime(chat.sentAt!!)

        }
        else
        {
            holder.rootLayout.apply {
                val lPram = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT, Gravity.START)
                layoutParams = lPram
            }

            holder.messageTextView.apply {
                setBackgroundResource(R.drawable.their_message)
                holder.messageTextView.text = chat.message
            }
/*
            val finalTime = chat.sentAt?.let { convertLongToTime(it.toLong()) }
            holder.messageDateTextView.text = finalTime
 */
            holder.messageDateTextView.text = convertTimestampToTime(chat.sentAt!!)

            holder.messageUsername.text = chat.username

            Firebase.firestore.collection(Constants.COLLECTION_USERS)
                .document(chat.userId!!)//peligro
                .get()
                .addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        it.result?.toObject(User::class.java)
                            ?.let { user: User ->
                                if(!user.profilePicturePath.isNullOrBlank()){
                                    Glide.with(holder.messageAvatar.context)
                                        .load(user.profilePicturePath)
                                        .into(holder.messageAvatar)
                                }
                            }
                    }
                }
        }
    }

    //Total items
    override fun getItemCount(): Int {
        return chatList.count()
    }

    //Maps view xml -> Kotlin
    inner class ChatViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.messageBody)
        val messageDateTextView: TextView = view.findViewById(R.id.messageDate)
        val messageAvatar: ImageView = view.findViewById(R.id.messageAvatar)
        val messageUsername: TextView = view.findViewById(R.id.messageUsername)
        val rootLayout: RelativeLayout = view.findViewById(R.id.RootLayout)

    }

    fun convertLongToTime(time: Long): String{
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm dd.MM.yyyy")
        return format.format(date)
    }

    fun convertTimestampToTime(time: Timestamp): String{
        val date = time.toDate()
        val format = SimpleDateFormat("HH:mm dd.MM.yyyy")
        return format.format(date)
    }
}