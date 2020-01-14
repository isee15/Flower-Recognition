package com.demo.flowerrecognition.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.flowerrecognition.R
import com.demo.flowerrecognition.adapter.FlowerSearchListAdapter
import com.demo.flowerrecognition.model.FlowerDataSource
import com.demo.flowerrecognition.model.FlowerItem
import com.demo.flowerrecognition.view.AZSideBarView
import kotlinx.android.synthetic.main.activity_search_list.*
import java.util.*
import kotlin.collections.ArrayList


class SearchListActivity : BaseActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var tableViewAdapter: FlowerSearchListAdapter
    var items: MutableList<FlowerItem> = ArrayList()
    var searchList: MutableList<FlowerItem> = ArrayList()
    private var sectionItemData: MutableList<Pair<String, MutableList<FlowerItem>>> =
        mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_list)
        setScreenTitle("搜索")

        searchView.setIconifiedByDefault(false)
//        searchView.setOnClickListener { searchView.setIconifiedByDefault(false) }

        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager


//        LoadAssetsTask(this).execute()

        val photosList = FlowerDataSource.allFlower
        val sortedList =
            photosList.sortedWith(compareBy { it.getPinyin() })
        items.addAll(sortedList)
        searchList.addAll(sortedList)


        resetSection()
        tableViewAdapter = FlowerSearchListAdapter(this, sectionItemData)
        //Apply this adapter to the RecyclerView
        recyclerView.adapter = tableViewAdapter


        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val pos = linearLayoutManager.findFirstVisibleItemPosition()
            if (pos > 0) {
                val (type, section, row) = tableViewAdapter.positionToSection(pos)
                floatSectionTv.visibility = View.VISIBLE
                floatSectionTv.text = sectionItemData[section].first
            } else {
                floatSectionTv.visibility = View.GONE
            }
        }

        searchView.setOnQueryTextListener(object :
            OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val imm =
                    this@SearchListActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchView.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    searchList.clear()
                    val searchText = newText.toLowerCase(Locale.getDefault())
                    items.forEach {
                        if (it.getPinyin()?.contains(searchText)!!
                            || it.nameCh.contains(searchText)
                            || it.nameEn.contains(searchText)
                        ) {
                            searchList.add(it)
                        }
                    }
                    resetSection()
                    recyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    searchList.clear()
                    searchList.addAll(items)
                    resetSection()
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return true
            }

        })
    }

    fun resetSection() {

        //This is the code to provide a sectioned list
        val sections: MutableList<Pair<String, MutableList<FlowerItem>>> =
            mutableListOf()

        var lastAlpha = ""
        var curItems: MutableList<FlowerItem> = mutableListOf()
        val letters: MutableList<String> = mutableListOf()
        for (value in searchList) {
            if (lastAlpha != value.getPinyin()?.first().toString()) {
                if (curItems.size > 0) {
                    sections.add(Pair(lastAlpha, curItems))
                }
                lastAlpha = value.getPinyin()?.first().toString()
                letters.add(lastAlpha.toUpperCase(Locale.getDefault()))
                curItems = mutableListOf()
            }
            curItems.add(value)
        }
        if (curItems.size > 0) {
            sections.add(Pair(lastAlpha, curItems))
        }
        sectionItemData.clear()
        sectionItemData.addAll(sections)


        barList.setLetters(letters)
        barList.setOnLetterChangeListener(object : AZSideBarView.OnLetterChangeListener {
            override fun onLetterChange(letter: String?) {
                var offset = 0
                for (value in sectionItemData) {
                    if (letter.equals(value.first, ignoreCase = true)) {
                        linearLayoutManager.scrollToPositionWithOffset(offset, 0)
//                        recyclerView.layoutManager?.scrollToPosition(value.sectionedPosition)
                        return
                    }
                    offset += value.second.size + 1
                }
            }
        })
    }


//    companion object {
//        class LoadAssetsTask internal constructor(private val context: SearchListActivity) :
//            AsyncTask<Void, Void, String?>() {
//
//
//            override fun onPreExecute() {
//
//            }
//
//            override fun doInBackground(vararg params: Void): String? {
//
//                return loadJSONFromAssets(context, "flower.json")
//            }
//
//
//            override fun onPostExecute(jsonString: String?) {
//                try {
//                    val gson = Gson()
//                    val sType = object : TypeToken<List<FlowerItem>>() {}.type
//                    val photosList = gson.fromJson<List<FlowerItem>>(jsonString, sType)
//                    val sortedList = photosList.sortedWith(compareBy { it.nameEn })
//                    context.adapter = RecyclerAdapter(context, sortedList)
//                    context.recyclerView.adapter = context.adapter
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//            }
//
//
//        }
//    }


}
