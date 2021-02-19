package com.e.got_compagnon.fragment

import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.e.got_compagnon.Constants
import com.e.got_compagnon.Constants.COLLECTION_CHAT
import com.e.got_compagnon.Constants.COLLECTION_USERS
import com.e.got_compagnon.R
import com.e.got_compagnon.adapter.ChatAdapter
import com.e.got_compagnon.model.Chat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var errorMessage: TextView
    private lateinit var errorBackground: TextView

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
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        messageEditText = view.findViewById<EditText>(R.id.messageEditText)
        sendButton = view.findViewById<Button>(R.id.sendButton)
        errorMessage = view.findViewById<TextView>(R.id.error_message)
        errorBackground = view.findViewById<TextView>(R.id.error_background)
    }

    override fun onStart() {
        super.onStart()
        checkUserAvailability()
    }

    private fun checkUserAvailability() {
        Firebase.auth.currentUser?.let {
            //User available
            errorBackground.visibility = View.GONE
            errorMessage.visibility = View.GONE
            //Init listeners
            initListeners()
            //Get chats
            getChats()

        } ?: run {
            recyclerView.visibility = View.GONE
            messageEditText.visibility = View.GONE
            sendButton.visibility = View.GONE
        }
    }

    private fun initRecyclerView() {
        //Layout manager
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        //Adapter
        chatAdapter = ChatAdapter(chatList = listOf())
        recyclerView.adapter = chatAdapter

        val docRef = firestore.collection(Constants.COLLECTION_CHAT)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null){
                Log.w(TAG, "Listen failed", e)
                return@addSnapshotListener
            }

            if(snapshot != null && !snapshot.isEmpty){
                getChats()
            }
            else{
                Log.d(TAG, "current data: null")
            }
        }
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
    }

    private fun sendMessage(message: String) {
        messageEditText.text.clear()
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
                                var url: String? = null

                                if(user.profilePicturePath!!.isNotBlank())
                                {
                                    url = user.profilePicturePath
                                }
                                val chat = Chat(
                                    userId = Firebase.auth.currentUser?.uid,
                                    message = message,
                                    sentAt = com.google.firebase.Timestamp.now(),
                                    //sentAt = Timestamp(Date().time.toLong()),
                                    isSent = false,
                                    imageUrl = null,
                                    username = user.username,
                                    avatarUrl = url
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
        firestore
            .collection(COLLECTION_CHAT)
            .orderBy("sentAt")
            //TODO: sort
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //update UI
                    val chats: List<Chat> =
                        it.result?.documents?.mapNotNull { it.toObject(Chat::class.java) }.orEmpty()

                    //chats.sortedBy { it.sentAt }

                    chatAdapter.chatList = chats
                    chatAdapter.notifyDataSetChanged()
                } else {
                    //TODO: show error
                }
            }

        recyclerView.scrollToPosition(chatAdapter.itemCount -1)
    }
}