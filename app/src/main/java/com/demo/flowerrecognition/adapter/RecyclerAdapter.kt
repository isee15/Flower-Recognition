package com.demo.flowerrecognition.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.flowerrecognition.R
import com.demo.flowerrecognition.activity.DetailActivity
import com.demo.flowerrecognition.inflate
import com.demo.flowerrecognition.model.FlowerItem
import com.demo.flowerrecognition.util.Utils
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*


class RecyclerAdapter(private val context: Context, private val flowerItems: List<FlowerItem>) :
    RecyclerView.Adapter<RecyclerAdapter.PhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return PhotoHolder(context, inflatedView)
    }

    override fun getItemCount() = flowerItems.size

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val itemPhoto = flowerItems[position]
        holder.bindPhoto(itemPhoto)
    }

    //1
    class PhotoHolder(private val context: Context, v: View) : RecyclerView.ViewHolder(v),
        View.OnClickListener {
        //2
        private var view: View = v
        private var flowerItem: FlowerItem? = null

        //3
        init {
            v.setOnClickListener(this)
        }

        //4
        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("item", this.flowerItem)
            context.startActivity(intent)

        }

        companion object {
            //5
//            private const val PHOTO_KEY = "PHOTO"
        }

        fun bindPhoto(flowerItem: FlowerItem) {
            this.flowerItem = flowerItem
            view.itemImage.setImageDrawable(Utils.getDrawable(context, "flower${flowerItem.ind}_0"))
            view.itemDate.text = flowerItem.nameEn
            view.itemDescription.text = flowerItem.nameCh
        }
    }

}
