package com.e.got_compagnon.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.e.got_compagnon.Constants.COLLECTION_CHAT
import com.e.got_compagnon.Constants.COLLECTION_USERS
import com.e.got_compagnon.R
import com.e.got_compagnon.adapter.ChatAdapter
import com.e.got_compagnon.model.Chat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var firestore: FirebaseFirestore

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Init firestore
        firestore = Firebase.firestore
        //Init views
        initViews(view)
        //Init RecyclerView
        initRecyclerView()
        //Init listeners
        initListeners()
        //Get chats
        getChats()
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        messageEditText = view.findViewById<EditText>(R.id.messageEditText)
        sendButton = view.findViewById<Button>(R.id.sendButton)
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
    }

    private fun initRecyclerView() {
        //Layout manager
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        //Adapter
        chatAdapter = ChatAdapter(chatList = listOf())
        recyclerView.adapter = chatAdapter
    }

    private fun initListeners() {
        //Send button
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            //validate
            if (message.isBlank()) return@setOnClickListener
            //send message
            sendMessage(message)
        }

        //Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            getChats()
        }
    }

    private fun sendMessage(message: String) {
        // 0 - Get uer Id
        Firebase.auth.currentUser?.uid?.let { userId: String ->
            //User available
            // 1 - Get user object
            firestore
                .collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result?.toObject(com.e.got_compagnon.model.User::class.java)
                            ?.let { user: com.e.got_compagnon.model.User ->
                                // 2 - Create chat message
                                val chat = Chat(
                                    userId = Firebase.auth.currentUser?.uid,
                                    message = message,
                                    sentAt = Date().time,
                                    isSent = false,
                                    imageUrl = null,
                                    username = user.username,
                                    avatarUrl = null //TODO: Support User Avatar
                                )
                                // 3 - Save Chat in Firestore
                                firestore
                                    .collection(COLLECTION_CHAT)
                                    .add(chat)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Log.i("Chat", "Succes uploading message $message")
                                            //update chat
                                            //no es la forma mas optima de hacerlo pero bueno
                                            getChats()
                                        } else {
                                            Log.w("Chat", "Error uploading message $message")
                                            //update chat
                                            //no es la forma mas optima de hacerlo pero bueno
                                            getChats()
                                        }
                                    }
                            } ?: run {
                            //TODO: Show error
                        }
                    } else {
                        //TODO: Show error
                    }
                }
        } ?: run {
            //User not available
            //TODO: show message
        }
    }

    private fun getChats() {
        swipeRefreshLayout.isRefreshing = true
        firestore
            .collection(COLLECTION_CHAT)
            //TODO: sort
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //update UI
                    val chats: List<Chat> =
                        it.result?.documents?.mapNotNull { it.toObject(Chat::class.java) }.orEmpty()
                    chatAdapter.chatList = chats
                    chatAdapter.notifyDataSetChanged()
                } else {
                    //TODO: show error
                }
                swipeRefreshLayout.isRefreshing = false
            }
    }
}