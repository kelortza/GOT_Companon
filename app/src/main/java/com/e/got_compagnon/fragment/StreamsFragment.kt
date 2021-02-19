package com.e.got_compagnon.fragment

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.got_compagnon.Constants
import com.e.got_compagnon.R
import com.e.got_compagnon.activity.TwitchLoginActivity
import com.e.got_compagnon.adapter.RecyclerAdapterStreams
import com.e.got_compagnon.model.Games
import com.e.got_compagnon.model.TWStreamResponse
import com.e.got_compagnon.network.Client
import com.e.got_compagnon.network.Endpoints
import com.e.got_compagnon.service.NetworkManager
import io.ktor.client.features.*
import io.ktor.client.features.get
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.security.auth.callback.Callback

class StreamsFragment: Fragment() {
    private val TAG = "StreamsFragment"
    private val BASE_URL = "https://api.twitch.tv/helix/"
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
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "++ onStart ++")
        checkUserAvailability(view!!)
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
            getTopGames(view)
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

   private fun getTopGames(view: View) {
       val httpClient = NetworkManager.createHttpClient()
       //viewLifecycleOwner.lifecycleScope.launch{
       //withContext(Dispatchers.IO){
       GlobalScope.launch(Dispatchers.IO) {
           try {
               val accessToken =
                   com.e.got_compagnon.service.UserManager(requireContext()).getAccessToken()
               val response = httpClient.get<Games>(BASE_URL + "games?name=GhostOfTsushima") {
                   header("Authorization", "Bearer $accessToken")
                   header("Client-Id", Constants.OAUTH_CLIENT_ID)
               }
               addToList(response.name, response.id, response.Image)

               /*
               val games = response.games
               for (game in games) {
                   Log.i(TAG, game.name!!)

                   addToList(game.name, game.id!!, game.box_art_url!!)
               }

                */
               Log.i(TAG, "Got top Games: $response")

               withContext(Dispatchers.Main) {
                   //TODO: Update UI
                   setUpRecyclerView(view)
               }
           } catch (t: Throwable) {
               //TODO: Handle error
               Log.i(TAG, t.message.toString())
           }
       }
   }
       //}
   //}
}