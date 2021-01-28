package com.e.got_compagnon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.e.got_compagnon.Interface.ItemClickListener
import com.e.got_compagnon.R
import com.e.got_compagnon.adapter.ViewHolder.ListSourceViewHolder
import com.e.got_compagnon.model.Website

class ListSourceAdapter(private val context:Context, private val webSite:Website):RecyclerView.Adapter<ListSourceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListSourceViewHolder {
        val inflater = LayoutInflater.from(parent!!.context)
        val itemView = inflater.inflate(R.layout.news_list, parent, false)
        return ListSourceViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ListSourceViewHolder, position: Int) {
        holder!!.source_title.text = webSite.sources!![position].name

        holder.setItemClickListenner(object: ItemClickListener
        {
            override fun onClick(view: View, position: Int) {
                Toast.makeText(context, "Will be implemented in next tutorial", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int {
        if(webSite != null) {
            return webSite.sources!!.size
        }
        else{
            return 0
        }
    }
}