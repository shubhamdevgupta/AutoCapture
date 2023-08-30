package com.example.autocapture

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerAdapter(var users: MutableList<ImageResponseItem>) :
    RecyclerView.Adapter<MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(myHolder: MyHolder, position: Int) {
        val user = users.get(position)
        myHolder.name.text = user.date

        // val decodedByteArray: ByteArray = Base64.decode(user, Base64.DEFAULT)
        val decodedByteArray: ByteArray = Base64.decode(user.image, Base64.DEFAULT)

        val decodedBitmap =
            BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)

        myHolder.image.setImageBitmap(decodedBitmap)

        Log.d("MYTAG", "onBindViewHolder: " + user)
    }
}

class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.tvText)
    val image = itemView.findViewById<ImageView>(R.id.ivImage)
}
