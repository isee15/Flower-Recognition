package com.demo.flowerrecognition.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.flowerrecognition.R
import com.demo.flowerrecognition.activity.DetailActivity
import com.demo.flowerrecognition.inflate
import com.demo.flowerrecognition.model.FlowerItem
import com.demo.flowerrecognition.util.Utils
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*

class FlowerSearchListAdapter(
    private val context: Context,
    private val flowerItems: List<Pair<String, List<FlowerItem>>>
) : TableViewAdapter() {


    override fun numberOfSections(): Int {
        return flowerItems.size
    }

    override fun numberInSection(section: Int): Int {
        return flowerItems[section].second.size
    }


    override fun rowHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return RowViewHolder(context, inflatedView)
    }

    override fun headerHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflatedView = parent.inflate(R.layout.section, false)
        return HeaderViewHolder(inflatedView)
    }

    override fun onBindHeaderHolder(holder: RecyclerView.ViewHolder, section: Int) {
        (holder as HeaderViewHolder).title.text = flowerItems[section].first
    }

    override fun onBindRowHolder(holder: RecyclerView.ViewHolder, section: Int, row: Int) {
        val itemPhoto = flowerItems[section].second[row]
        (holder as RowViewHolder).bindPhoto(itemPhoto)
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.section_text)

    }


    //1
    class RowViewHolder(private val context: Context, v: View) : RecyclerView.ViewHolder(v),
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

        fun bindPhoto(flowerItem: FlowerItem) {
            this.flowerItem = flowerItem
            view.itemImage.setImageDrawable(Utils.getDrawable(context, "flower${flowerItem.ind}_0"))
            view.itemDate.text = flowerItem.nameEn
            view.itemDescription.text = flowerItem.nameCh
        }
    }

}
