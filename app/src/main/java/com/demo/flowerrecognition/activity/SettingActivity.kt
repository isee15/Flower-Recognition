package com.demo.flowerrecognition.activity

import android.content.pm.PackageManager
import android.os.Bundle
import com.demo.flowerrecognition.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setScreenTitle("设置")
        val manager = this.packageManager
        val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)
        textViewVersion.text = "${resources.getString(R.string.app_name)} V${info.versionName}"
    }
}
