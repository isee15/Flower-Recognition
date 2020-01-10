package com.demo.flowerrecognition.activity

import android.os.Bundle
import android.view.View.GONE
import com.demo.flowerrecognition.R
import com.demo.flowerrecognition.model.FlowerItem
import com.demo.flowerrecognition.util.Utils
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val item = intent.getParcelableExtra<FlowerItem>("item")
        textViewNameCn.text = item.nameCh
        textViewNameEn.text = item.nameEn
        if (item.language.isNotEmpty()) {
            textViewLang.text = "花语 -- ${item.language}"
        }
        else {
            textViewLang.visibility = GONE
        }
        textViewGenusFamily.text = "${item.genusCh} ${item.familyCh}"
        textViewLiterary.text = item.literary
        textViewDesc.text = item.description
        imageView.setImageDrawable(Utils.getDrawable(this, "flower${item.ind}_0"))
        setScreenTitle("花卉信息")
    }
}
