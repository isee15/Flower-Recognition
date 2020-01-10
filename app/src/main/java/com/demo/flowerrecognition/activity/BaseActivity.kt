package com.demo.flowerrecognition.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.demo.flowerrecognition.R
import com.demo.flowerrecognition.view.ProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var mTextViewScreenTitle: TextView
    private lateinit var mImageButtonBack: ImageButton
    private val progressBar = ProgressBar()
    protected val job = SupervisorJob() // the instance of a Job for this activity
    override val coroutineContext = Dispatchers.Main.immediate + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)

        mTextViewScreenTitle = findViewById(R.id.text_screen_title) as TextView
        mImageButtonBack = findViewById(R.id.image_back_button)
        this.mImageButtonBack.setOnClickListener {
            this.onBackPressed()
        }

        val toolbar: androidx.appcompat.widget.Toolbar =
            findViewById(R.id.toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
            val statusId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (statusId > 0) {
                val statusHeight = resources.getDimensionPixelSize(statusId)
                // Calculate ActionBar height
                val tv = TypedValue()
                if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    val actionBarHeight =
                        TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
                    toolbar.layoutParams =
                        LinearLayout.LayoutParams(
                            MATCH_PARENT,
                            actionBarHeight + statusHeight
                        )
                }
            }

            toolbar.setOnApplyWindowInsetsListener { _, insets ->

                toolbar.setPadding(
                    0,
                    insets.systemWindowInsetTop,
                    0,
                    0
                )

                insets.consumeSystemWindowInsets()

            }
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    fun setScreenTitle(resId: Int) {
        mTextViewScreenTitle.text = getString(resId)
    }

    fun setScreenTitle(title: String) {
        mTextViewScreenTitle.text = title
    }

    fun getBackButton(): ImageButton {
        return mImageButtonBack
    }

    fun showProgressDialog(title: String) {
        progressBar.show(this, title)

    }

    fun dismissProgressDialog() {
        if (progressBar.dialog.isShowing) {
            progressBar.dialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // cancel the job when activity is destroyed
    }
}