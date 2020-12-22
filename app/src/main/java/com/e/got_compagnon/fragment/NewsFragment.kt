package com.e.got_compagnon.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.e.got_compagnon.Common.Common
import com.e.got_compagnon.Interface.NewsService
import com.e.got_compagnon.MainActivity
import com.e.got_compagnon.R
import com.e.got_compagnon.adapter.ListSourceAdapter
import com.e.got_compagnon.model.Website
import com.google.gson.Gson
import dmax.dialog.SpotsDialog
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Response

class NewsFragment: Fragment() {

    lateinit var layoutManager: LinearLayoutManager
    lateinit var mService: NewsService
    lateinit var adapter: ListSourceAdapter //esto lo acabare borrando porque me la sudan las sources
    //lateinit var dialog:AlertDialog

    lateinit var swipe_to_refresh: SwipeRefreshLayout
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Init cache DB
        val appCOntext = context!!.applicationContext
        Paper.init(appCOntext)

        //Init Service
        mService = Common.newsService

        //Init View
        swipe_to_refresh = view.findViewById(R.id.swipeRefreshNews)
        recyclerView = view.findViewById(R.id.recyclerViewNews)

        //Init listenners
        swipe_to_refresh.setOnRefreshListener {
            loadWebSiteSource(true)
        }

        recyclerView.setHasFixedSize(true)
        layoutManager= LinearLayoutManager(appCOntext)
        recyclerView.layoutManager=layoutManager

        //dialog = SpotsDialog(MainActivity.this)

        loadWebSiteSource(false)
    }

    private fun loadWebSiteSource(isRefresh: Boolean) {

        if(!isRefresh){
            val cache = Paper.book().read<String>("cache")
            if(cache != null && !cache.isBlank() && cache != "null"){
                //read cache
                val website = Gson().fromJson<Website>(cache, Website::class.java)
                adapter = ListSourceAdapter(activity!!.baseContext, website)
                adapter.notifyDataSetChanged()
                recyclerView.adapter = adapter
            }
            else{
                //load website and write cache
                //dialog.show()
                //fetch new data
                mService.sources.enqueue(object:retrofit2.Callback<Website>{
                    override fun onResponse(call: Call<Website>, response: Response<Website>) {
                        adapter = ListSourceAdapter(activity!!.baseContext, response!!.body()!!)
                        adapter.notifyDataSetChanged()
                        recyclerView.adapter = adapter

                        //Save to cache
                        Paper.book().write("cache", Gson().toJson(response!!.body()!!))

                        //dialog.dismiss()
                    }

                    override fun onFailure(call: Call<Website>, t: Throwable) {
                        Toast.makeText(activity!!.baseContext, "Failed", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
        else{


            swipe_to_refresh.isRefreshing=true

            mService.sources.enqueue(object:retrofit2.Callback<Website>{
                override fun onResponse(call: Call<Website>, response: Response<Website>) {
                    adapter = ListSourceAdapter(activity!!.baseContext, response!!.body()!!)
                    adapter.notifyDataSetChanged()
                    recyclerView.adapter = adapter

                    //Save to cache
                    Paper.book().write("cache", Gson().toJson(response!!.body()!!))

                    swipe_to_refresh.isRefreshing=false
                }

                override fun onFailure(call: Call<Website>, t: Throwable) {
                    Toast.makeText(activity!!.baseContext, "Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
}