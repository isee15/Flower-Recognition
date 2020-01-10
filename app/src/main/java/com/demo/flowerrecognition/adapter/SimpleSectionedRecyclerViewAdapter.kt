package com.demo.flowerrecognition.adapter

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import java.util.*


class SimpleSectionedRecyclerViewAdapter(
    context: Context, sectionResourceId: Int, textResourceId: Int,
    baseAdapter: Adapter<ViewHolder>
) : Adapter<ViewHolder>() {
    private val mContext: Context
    private var mValid = true
    private val mSectionResourceId: Int
    private val mTextResourceId: Int
    private val mLayoutInflater: LayoutInflater
    private val mBaseAdapter: Adapter<ViewHolder>
    private val mSections =
        SparseArray<Section?>()

    class SectionViewHolder(view: View, mTextResourceid: Int) : ViewHolder(view) {
        var title: TextView = view.findViewById(mTextResourceid)

    }

    override fun onCreateViewHolder(parent: ViewGroup, typeView: Int): ViewHolder {
        return if (typeView == SECTION_TYPE) {
            val view: View =
                LayoutInflater.from(mContext).inflate(mSectionResourceId, parent, false)
            SectionViewHolder(view, mTextResourceId)
        } else {
            mBaseAdapter.onCreateViewHolder(parent, typeView - 1)
        }
    }

    override fun onBindViewHolder(
        sectionViewHolder: ViewHolder,
        position: Int
    ) {
        if (isSectionHeaderPosition(position)) {
            (sectionViewHolder as SectionViewHolder).title.text = mSections[position]!!.title
        } else {
            mBaseAdapter.onBindViewHolder(sectionViewHolder, sectionedPositionToPosition(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSectionHeaderPosition(position)) SECTION_TYPE else mBaseAdapter.getItemViewType(
            sectionedPositionToPosition(position)
        ) + 1
    }

    class Section(var firstPosition: Int, var title: CharSequence) {
        var sectionedPosition = 0

    }

    fun setSections(sections: Array<Section>) {
        mSections.clear()
        Arrays.sort(sections
        ) { o, o1 -> if (o.firstPosition == o1.firstPosition) 0 else if (o.firstPosition < o1.firstPosition) -1 else 1 }
        for ((offset, section) in sections.withIndex()) {
            section.sectionedPosition = section.firstPosition + offset
            mSections.append(section.sectionedPosition, section)
        }
        notifyDataSetChanged()
    }

    fun positionToSectionedPosition(position: Int): Int {
        var offset = 0
        for (i in 0 until mSections.size()) {
            if (mSections.valueAt(i)!!.firstPosition > position) {
                break
            }
            ++offset
        }
        return position + offset
    }

    fun sectionedPositionToPosition(sectionedPosition: Int): Int {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return NO_POSITION
        }
        var offset = 0
        for (i in 0 until mSections.size()) {
            if (mSections.valueAt(i)!!.sectionedPosition > sectionedPosition) {
                break
            }
            --offset
        }
        return sectionedPosition + offset
    }

    fun isSectionHeaderPosition(position: Int): Boolean {
        return mSections[position] != null
    }

    override fun getItemId(position: Int): Long {
        return if (isSectionHeaderPosition(position)) (Int.MAX_VALUE - mSections.indexOfKey(
            position
        )).toLong() else mBaseAdapter.getItemId(sectionedPositionToPosition(position))
    }

    override fun getItemCount(): Int {
        return if (mValid) mBaseAdapter.itemCount + mSections.size() else 0
    }

    companion object {
        private const val SECTION_TYPE = 0
    }

    init {
        mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mSectionResourceId = sectionResourceId
        mTextResourceId = textResourceId
        mBaseAdapter = baseAdapter
        mContext = context
        mBaseAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                mValid = mBaseAdapter.itemCount > 0
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(
                positionStart: Int,
                itemCount: Int
            ) {
                mValid = mBaseAdapter.itemCount > 0
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(
                positionStart: Int,
                itemCount: Int
            ) {
                mValid = mBaseAdapter.itemCount > 0
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(
                positionStart: Int,
                itemCount: Int
            ) {
                mValid = mBaseAdapter.itemCount > 0
                notifyItemRangeRemoved(positionStart, itemCount)
            }
        })
    }
}