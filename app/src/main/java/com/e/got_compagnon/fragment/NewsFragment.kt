package com.e.got_compagnon.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.got_compagnon.Interface.NewsService
import com.e.got_compagnon.R
import com.e.got_compagnon.adapter.RecyclerAdapter
import com.squareup.okhttp.OkHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Time
import java.util.concurrent.TimeUnit

class NewsFragment: Fragment() {

    //private val BASE_URL = "https://api.currentsapi.services"
    private val BASE_URL = "https://newsapi.org"
    private val TAG = "NewsFragment"

    private lateinit var recyclerView: RecyclerView
    lateinit var countDownTimer: CountDownTimer

    private var titleList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imageList = mutableListOf<String>()
    private var linksList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "++ onCreateView ++")
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "++ onViewCreated ++")
        //Init views
        initViews(view)
        //Init Listeners
        //initListeners()
        //make API Request
        makeAPIRequest(view)

    }
    //TODO: poner todo esto dentro del initViews / initListeners
    private fun setUpRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewNews)
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.adapter = RecyclerAdapter(titleList, descList, imageList, linksList)
    }

    private fun addToList(title: String, description: String, image: String, link: String){
        titleList.add(title)
        descList.add(description)
        imageList.add(image)
        linksList.add(link)
    }

    private fun makeAPIRequest(view: View) {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsService::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getNews()
                for (article in response.articles) {
                    Log.i(TAG, "Result = $article")
                    addToList(article.title,article.description,article.urlToImage,article.url)
                }

                withContext(Dispatchers.Main){
                    setUpRecyclerView(view)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun initViews(parentView: View){


    }

    private fun initListeners() {

    }
}