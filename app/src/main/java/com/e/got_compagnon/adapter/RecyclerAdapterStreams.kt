package com.e.got_compagnon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.got_compagnon.R

class RecyclerAdapterStreams(
    private var titles: List<String>,
    private var usernames: List<String>,
    private var thumbnails: List<String>
) : RecyclerView.Adapter<RecyclerAdapterStreams.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val itemUsername: TextView = view.findViewById<TextView>(R.id.stream_username)
        val itemThumbnail: ImageView = view.findViewById<ImageView>(R.id.stream_thumbnail)
        val itemTitle: TextView = view.findViewById<TextView>(R.id.stream_title)

        init{ view.setOnClickListener{ v: View->
            //TODO: Open new activity with stream details
        }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun onBindViewHolder(holder: RecyclerAdapterStreams.ViewHolder, position: Int) {
        holder.itemTitle.text = titles[position]
        holder.itemUsername.text = usernames[position]

        Glide.with(holder.itemThumbnail)
            .load(thumbnails[position])
            .into(holder.itemThumbnail)
    }
}