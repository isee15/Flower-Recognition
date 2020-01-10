package com.demo.flowerrecognition.activity

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.flowerrecognition.R
import com.demo.flowerrecognition.adapter.RecyclerAdapter
import com.demo.flowerrecognition.adapter.SimpleSectionedRecyclerViewAdapter
import com.demo.flowerrecognition.model.FlowerDataSource
import com.demo.flowerrecognition.model.FlowerItem
import com.demo.flowerrecognition.view.AZSideBarView
import kotlinx.android.synthetic.main.activity_search_list.*


class SearchListActivity : BaseActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerAdapter
    private lateinit var sectionAdapter: SimpleSectionedRecyclerViewAdapter
    var items: MutableList<FlowerItem> = ArrayList()
    var search_list: MutableList<FlowerItem> = ArrayList()
    private lateinit var sectionArray: Array<SimpleSectionedRecyclerViewAdapter.Section>

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
        search_list.addAll(sortedList)

        adapter = RecyclerAdapter(this, search_list)

        sectionAdapter =
            SimpleSectionedRecyclerViewAdapter(
                this,
                R.layout.section,
                R.id.section_text,
                this.adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
            )
        resetSection()

        //Apply this adapter to the RecyclerView
        recyclerView.adapter = sectionAdapter


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
                    search_list.clear()
                    val searchText = newText.toLowerCase()
                    items.forEach {
                        if (it.getPinyin()?.contains(searchText)!!
                            || it.nameCh.contains(searchText)
                            || it.nameEn.contains(searchText)
                        ) {
                            search_list.add(it)
                        }
                    }
                    resetSection()
                    recyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    search_list.clear()
                    search_list.addAll(items)
                    resetSection()
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return true
            }

        })
    }

    fun resetSection() {

        //This is the code to provide a sectioned list
        val sections: MutableList<SimpleSectionedRecyclerViewAdapter.Section> =
            ArrayList()

        var lastAlpha = ""
        var firstPosition: Int
        val letters: MutableList<String> = ArrayList()
        for ((index, value) in search_list.withIndex()) {
            if (lastAlpha != value.getPinyin()?.first().toString()) {
                lastAlpha = value.getPinyin()?.first().toString()
                letters.add(lastAlpha.toUpperCase());
                firstPosition = index
                //Sections
                sections.add(
                    SimpleSectionedRecyclerViewAdapter.Section(
                        firstPosition,
                        lastAlpha.toUpperCase()
                    )
                )
            }
        }

        sectionArray = sections.toTypedArray()
        sectionAdapter.setSections(sectionArray)

        barList.setLetters(letters)
        barList.setOnLetterChangeListener(object : AZSideBarView.OnLetterChangeListener {
            override fun onLetterChange(letter: String?) {
                for (value in sectionArray) {
                    if (letter.equals(value.title.toString(), ignoreCase = true)) {
                        linearLayoutManager.scrollToPositionWithOffset(value.sectionedPosition,0)
//                        recyclerView.layoutManager?.scrollToPosition(value.sectionedPosition)
                        return
                    }
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
