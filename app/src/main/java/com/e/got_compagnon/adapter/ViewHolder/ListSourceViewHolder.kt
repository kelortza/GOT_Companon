package com.e.got_compagnon.adapter.ViewHolder

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e.got_compagnon.Interface.ItemClickListener
import com.e.got_compagnon.R


class ListSourceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private lateinit var itemClickListener:ItemClickListener

    var source_title = itemView.findViewById<TextView>(R.id.newsName)

    fun setItemClickListenner(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }

    override fun onClick(v: View?) {
        itemClickListener.onClick(v!!, adapterPosition)
    }
}