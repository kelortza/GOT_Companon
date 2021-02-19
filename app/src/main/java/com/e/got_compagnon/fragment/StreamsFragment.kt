package com.e.got_compagnon.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.got_compagnon.R
import com.e.got_compagnon.activity.TwitchLoginActivity
import com.e.got_compagnon.adapter.RecyclerAdapterStreams
import com.e.got_compagnon.model.TWStreamResponse
import com.e.got_compagnon.network.Client
import com.e.got_compagnon.network.Endpoints
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class StreamsFragment: Fragment() {
    private val TAG = "StreamsFragment"
    private lateinit var twictLoginButton: Button
    private lateinit var recyclerView: RecyclerView

    private var titleList = mutableListOf<String>()
    private var userNameList = mutableListOf<String>()
    private var thumbnailList = mutableListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_streams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initListeners()
        checkUserAvailability(view)
    }

    private fun initViews(view: View){
        twictLoginButton = view.findViewById(R.id.twitchLoginButton)
    }

    private fun initListeners(){
        twictLoginButton.setOnClickListener {
            startActivity(Intent(requireActivity(), TwitchLoginActivity::class.java))
        }
    }

    private fun checkUserAvailability(view: View){
        val isLoggedIn = com.e.got_compagnon.service.UserManager(requireContext()).getAccessToken() != null
        if(isLoggedIn){
            twictLoginButton.visibility = View.GONE
            setUpRecyclerView(view)
            makeAPIRequest(view)
        } else {
            twictLoginButton.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView(view: View){
        recyclerView = view.findViewById(R.id.streamsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.adapter = RecyclerAdapterStreams(titleList, userNameList, thumbnailList)
    }

    private fun  addToList(title: String, username: String, image: String){
        titleList.add(title)
        userNameList.add(username)
        thumbnailList.add(image)
    }

    private fun makeAPIRequest(view: View){
        Client.service.getStreams().enqueue(Client : Callback<TWStreamResponse>{
            override fun onResponse(call: Call<TWStreamResponse>, response: Response<TWStreamResponse>){
                response.body()?.data?.let{streams->
                    for(stream in streams){
                        Log.i(TAG, "Title:${stream.title} and image: ${stream.imageUrl} and username: ${stream.username}")
                        Log.i(TAG, "Stream Url: https://www.twitch.tv/${stream.username}")
                    }
                }
            }

            override fun onFailure(call: Call<TWStreamResponse>, t: Throwable){
                t.printStackTrace()
            }
        })
    }
}