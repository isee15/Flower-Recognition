package com.demo.flowerrecognition.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.demo.flowerrecognition.model.FlowerItem

abstract class TableViewAdapter(private val context: Context, private val flowerItems: List<FlowerItem>) :
    RecyclerView.Adapter<RecyclerAdapter.PhotoHolder>() {

}