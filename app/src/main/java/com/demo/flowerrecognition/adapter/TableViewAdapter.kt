package com.demo.flowerrecognition.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class TableViewAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val SECTION_TYPE = 0
        private const val ROW_TYPE = 1
    }


    abstract fun numberOfSections(): Int
    abstract fun numberInSection(section: Int): Int
    abstract fun rowHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    abstract fun headerHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    abstract fun onBindHeaderHolder(holder: RecyclerView.ViewHolder, section: Int)
    abstract fun onBindRowHolder(holder: RecyclerView.ViewHolder, section: Int, row: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SECTION_TYPE) {
            headerHolder(parent, viewType)
        } else {
            rowHolder(parent, viewType)
        }
    }

    override fun getItemCount(): Int {
        var count = 0
        for (i in 0 until numberOfSections()) {
            count += numberInSection(i) + 1
        }
        return count
    }

    private fun positionToSection(position: Int): Triple<Int, Int, Int> {
        var offset = position
        for (i in 0 until numberOfSections()) {
            val rows = numberInSection(i) + 1
            when {
                offset > rows -> {
                    offset -= rows
                }
                offset == rows -> {
                    return Triple(SECTION_TYPE, i + 1, 0)
                }
                else -> {
                    if (offset < 1) {
                        return Triple(SECTION_TYPE, i, 0)
                    }
                    return Triple(ROW_TYPE, i, offset - 1)
                }
            }

        }
        return Triple(SECTION_TYPE, 0, 0)
    }

    override fun getItemViewType(position: Int): Int {
        val (type, _, _) = positionToSection(position)
        return type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (type, section, row) = positionToSection(position)
        if (type == SECTION_TYPE) {
            onBindHeaderHolder(holder, section)
        } else if (type == ROW_TYPE) {
            onBindRowHolder(holder, section, row)
        }
    }

}