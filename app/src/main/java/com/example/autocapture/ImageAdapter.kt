package com.example.autocapture

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class RecyclerviewItemAdapter internal constructor(private val itemsList: List<ImageResponseItem>) :
    RecyclerView.Adapter<RecyclerviewItemAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemsList[position]
        holder.date.text = item.date
        Log.d("MYTAG", "onBindViewHolder: date fetch "+item.date)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var date: TextView = itemView.findViewById(R.id.tvText)
        var image: ImageView = itemView.findViewById(R.id.ivImage)
    }
}