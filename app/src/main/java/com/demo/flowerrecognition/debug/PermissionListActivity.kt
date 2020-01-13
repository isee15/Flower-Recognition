package com.demo.flowerrecognition.debug

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.flowerrecognition.R
import com.demo.flowerrecognition.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_search_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.schedule

class PermissionListActivity : BaseActivity() {

    private lateinit var sectionAdapter: PermissionListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_list)

        recyclerView.layoutManager = LinearLayoutManager(this)

        sectionAdapter = PermissionListAdapter(this)
        recyclerView.adapter = sectionAdapter

        setScreenTitle("权限")


        Timer("SettingUp", false).schedule(500) {

            launch {
                withContext(Dispatchers.Main) {
                    sectionAdapter.notifyDataSetChanged()
                }
            }

        }
    }

    // Custom method to get app requested and granted permissions from package name
    private fun getPermissionsByPackageName(packageName: String): MutableList<Pair<String, String>> { // Initialize a new string builder instance
        val ret = mutableListOf<Pair<String, String>>()
        val descMap =
            HashMap<String, String?>()
        descMap["ACCEPT_HANDOVER"] = "允许呼叫应用继续在另一个应用中启动的呼叫"
        descMap["ACCESS_CHECKIN_PROPERTIES"] = "允许对迁入数据库中的“属性”表进行读/写访问，以更改上载的值。"
        descMap["ACCESS_COARSE_LOCATION"] = "允许应用访问大致位置。"
        descMap["ACCESS_FINE_LOCATION"] = "允许应用访问精确位置。"
        descMap["ACCESS_LOCATION_EXTRA_COMMANDS"] = "允许应用程序访问额外的位置提供程序命令。"
        descMap["ACCESS_NETWORK_STATE"] = "允许应用程序访问有关网络的信息。"
        descMap["ACCESS_NOTIFICATION_POLICY"] = "对希望访问通知政策的应用程序的标记权限。"
        descMap["ACCESS_WIFI_STATE"] = "允许应用程序访问有关Wi-Fi网络的信息。"
        descMap["ACCOUNT_MANAGER"] = "允许应用程序调用AccountAuthenticators。不适用于第三方应用程序。"
        descMap["ADD_VOICEMAIL"] = "允许应用程序将语音邮件添加到系统中。"
        descMap["ANSWER_PHONE_CALLS"] = "允许该应用接听来电。"
        descMap["BATTERY_STATS"] = "允许应用程序收集电池统计信息"
        descMap["BIND_ACCESSIBILITY_SERVICE"] = "Android系统的可访问性服务"
        descMap["BIND_APPWIDGET"] = "允许应用程序告诉AppWidget服务哪个应用程序可以访问AppWidget的数据。"
        descMap["BIND_AUTOFILL_SERVICE"] = "允许程序内容被自动填写"
        descMap["BIND_CARRIER_MESSAGING_SERVICE"] = "【已弃用】允许绑定到运营商应用程序中的服务的系统进程将有这个权限"
        descMap["BIND_CARRIER_SERVICES"] = "允许绑定到运营商应用程序中的服务的系统进程将具有此权限。"
        descMap["BIND_CHOOSER_TARGET_SERVICE"] = "允许用户在一个应用里面直接分享内容到其他地方"
        descMap["BIND_CONDITION_PROVIDER_SERVICE"] = "提供有关布尔状态的条件的服务。"
        descMap["BIND_DEVICE_ADMIN"] = "设备管理接收器"
        descMap["BIND_DREAM_SERVICE"] = "允许设备闲置、充电或锁屏时，可以显示特定的内容"
        descMap["BIND_INCALL_SERVICE"] = "允许提供用于管理电话呼叫的用户界面"
        descMap["BIND_INPUT_METHOD"] = "输入法服务权限"
        descMap["BIND_MIDI_DEVICE_SERVICE"] = "允许实现虚拟MIDI设备的服务"
        descMap["BIND_NFC_SERVICE"] = "允许模拟Android服务组件内的NFC卡"
        descMap["BIND_NOTIFICATION_LISTENER_SERVICE"] = "允许接收在发布或删除新通知或其排名发生变化时来自系统的呼叫的服务。"
        descMap["BIND_PRINT_SERVICE"] = "允许实现打印服务"
        descMap["BIND_QUICK_SETTINGS_TILE"] = "允许应用程序绑定到第三方快速设置磁贴。"
        descMap["BIND_REMOTEVIEWS"] = "允许连接到远程适配器"
        descMap["BIND_SCREENING_SERVICE"] = "允许来电过滤"
        descMap["BIND_TELECOM_CONNECTION_SERVICE"] = "管理Android系统当前的通话,如来电显示，接听电话，挂断电话"
        descMap["BIND_TEXT_SERVICE"] = "允许创建拼写检查器类"
        descMap["BIND_TV_INPUT"] = "允许实现电视输入服务"
        descMap["BIND_VISUAL_VOICEMAIL_SERVICE"] = "实现处理OMTP或类似可视语音邮件的拨号程序服务"
        descMap["BIND_VOICE_INTERACTION"] = "全局语音交互服务"
        descMap["BIND_VPN_SERVICE"] = "允许运行VPN解决方案"
        descMap["BIND_VR_LISTENER_SERVICE"] = "虚拟现实（VR）服务"
        descMap["BIND_WALLPAPER"] = "壁纸服务"
        descMap["BLUETOOTH"] = "允许应用程序连接到配对的蓝牙设备。"
        descMap["BLUETOOTH_ADMIN"] = "允许应用程序发现并配对蓝牙设备。"
        descMap["BLUETOOTH_PRIVILEGED"] = "允许应用程序在没有用户交互的情况下配对蓝牙设备"
        descMap["BODY_SENSORS"] = "允许应用程序访问用户用来测量身体内部情况的传感器数据，例如心率。"
        descMap["BODY_SENSORS"] = "允许应用程序访问用户用来测量身体内部情况的传感器数据，例如心率。"
        descMap["BROADCAST_PACKAGE_REMOVED"] = "允许应用程序广播已删除应用程序包的通知。"
        descMap["BROADCAST_SMS"] = "允许应用程序广播SMS收据通知。"
        descMap["BROADCAST_STICKY"] = "允许应用程序广播粘性意图。"
        descMap["BROADCAST_WAP_PUSH"] = "允许应用程序广播WAP"
        descMap["CALL_COMPANION_APP"] = "允许实现InCallServiceAPI"
        descMap["CALL_PHONE"] = "允许应用程序在不通过拨号器用户界面的情况下发起电话呼叫，以便用户确认呼叫。"
        descMap["CALL_PRIVILEGED"] = "允许应用程序拨打任何电话号码（包括紧急号码），而无需通过拨号器用户界面以便用户确认正在进行的呼叫。"
        descMap["CAMERA"] = "必须能够访问相机设备。"
        descMap["CAPTURE_AUDIO_OUTPUT"] = "允许应用程序捕获音频输出。"
        descMap["CHANGE_COMPONENT_ENABLED_STATE"] = "允许应用程序更改是否启用应用程序组件（除了它自己的组件）。"
        descMap["CHANGE_CONFIGURATION"] = "允许应用程序修改当前配置，例如区域设置。"
        descMap["CHANGE_NETWORK_STATE"] = "允许应用程序更改网络连接状态。"
        descMap["CHANGE_WIFI_MULTICAST_STATE"] = "允许应用程序进入Wi-Fi多播模式。"
        descMap["CHANGE_WIFI_STATE"] = "允许应用程序更改Wi-Fi连接状态。"
        descMap["CLEAR_APP_CACHE"] = "允许应用程序清除设备上所有已安装应用程序的缓存。"
        descMap["CONTROL_LOCATION_UPDATES"] = "允许从收音机启用/禁用位置更新通知。"
        descMap["DELETE_CACHE_FILES"] = "删除应用程序缓存文件的旧权限，不再使用，但是让我们静静地忽略调用而不是抛出异常的信号。"
        descMap["DELETE_PACKAGES"] = "允许应用程序删除包。"
        descMap["DIAGNOSTIC"] = "允许应用程序将RW转换为诊断资源。"
        descMap["DISABLE_KEYGUARD"] = "允许应用程序在密钥保护不安全时禁用它。"
        descMap["DUMP"] = "允许应用程序从系统服务检索状态转储信息。"
        descMap["EXPAND_STATUS_BAR"] = "允许应用程序展开或折叠状态栏。"
        descMap["FACTORY_TEST"] = "作为制造商测试应用程序运行，以root用户身份运行。"
        descMap["FOREGROUND_SERVICE"] = "允许常规应用程序使用Service.startForeground。"
        descMap["GET_ACCOUNTS"] = "允许访问“帐户服务”中的帐户列表。"
        descMap["GET_ACCOUNTS_PRIVILEGED"] = "允许访问“帐户服务”中的帐户列表。"
        descMap["GET_AND_REQUEST_SCREEN_LOCK_COMPLEXITY"] = "允许应用程序获得屏幕锁定复杂性并提示用户将屏幕锁定更新到某个复杂程度。"
        descMap["GET_PACKAGE_SIZE"] = "允许应用程序查找任何包使用的空间。"
        descMap["GET_TASKS"] = "获取任务，此常量在API级别21中已弃用。"
        descMap["GLOBAL_SEARCH"] = "允许全局搜索系统访问其数据。"
        descMap["INSTALL_LOCATION_PROVIDER"] = "允许应用程序将位置提供程序安装到位置管理器中。"
        descMap["INSTALL_PACKAGES"] = "允许应用程序安装包。"
        descMap["INSTALL_SHORTCUT"] = "允许应用程序在Launcher中安装快捷方式。"
        descMap["INSTANT_APP_FOREGROUND_SERVICE"] = "允许即时应用创建前台服务。"
        descMap["INTERNET"] = "允许应用程序打开网络套接字。"
        descMap["KILL_BACKGROUND_PROCESSES"] = "允许应用程序呼叫"
        descMap["LOCATION_HARDWARE"] = "允许应用程序在硬件中使用位置功能"
        descMap["MANAGE_DOCUMENTS"] = "允许应用程序管理对文档的访问，通常作为文档选择器的一部分。"
        descMap["MANAGE_OWN_CALLS"] = "允许通过自我管理的ConnectionServiceAPI"
        descMap["MASTER_CLEAR"] = "恢复出厂设置"
        descMap["MEDIA_CONTENT_CONTROL"] = "允许应用程序知道正在播放的内容并控制其播放。"
        descMap["MODIFY_AUDIO_SETTINGS"] = "允许应用程序修改全局音频设置。"
        descMap["MODIFY_PHONE_STATE"] = "允许修改电话状态"
        descMap["MOUNT_FORMAT_FILESYSTEMS"] = "允许为可移动存储格式化文件系统。"
        descMap["MOUNT_UNMOUNT_FILESYSTEMS"] = "允许安装和卸载可移动存储的文件系统。"
        descMap["NFC"] = "允许应用程序通过NFC执行I"
        descMap["NFC_TRANSACTION_EVENT"] = "允许应用程序接收NFC交易事件。"
        descMap["PACKAGE_USAGE_STATS"] = "允许应用程序收集组件使用统计信息"
        descMap["PERSISTENT_ACTIVITY"] = "允许应用程序使其活动持久化。"
        descMap["PROCESS_OUTGOING_CALLS"] = "允许应用程序查看拨出呼叫期间拨打的号码，并选择将呼叫重定向到其他号码或完全中止呼叫."
        descMap["READ_CALENDAR"] = "允许应用程序读取用户的日历数据。"
        descMap["READ_CALL_LOG"] = "允许应用程序读取用户的通话记录。"
        descMap["READ_CONTACTS"] = "允许应用程序读取用户的联系人数据。"
        descMap["READ_EXTERNAL_STORAGE"] = "允许应用程序从外部存储读取。"
        descMap["READ_EXTERNAL_STORAGE"] = "允许应用程序从外部存储读取。"
        descMap["READ_INPUT_STATE"] = "允许应用程序检索密钥和交换机的当前状态。"
        descMap["READ_LOGS"] = "允许应用程序读取低级系统日志文件。"
        descMap["READ_MEDIA_AUDIO"] = "允许应用程序读取用户的共享音频集合。"
        descMap["READ_MEDIA_IMAGES"] = "允许应用程序读取用户的共享图像集合。"
        descMap["READ_MEDIA_VIDEO"] = "允许应用程序读取用户的共享视频集。"
        descMap["READ_PHONE_NUMBERS"] = "允许读取设备的电话号码。"
        descMap["READ_PHONE_STATE"] =
            "允许只读访问电话状态，包括设备的电话号码，当前的蜂窝网络信息，任何正在进行的呼叫的状态以及设备上注册的任何PhoneAccounts列表。"
        descMap["READ_SMS"] = "允许应用程序读取SMS消息。"
        descMap["READ_SYNC_SETTINGS"] = "允许应用程序读取同步设置。"
        descMap["READ_SYNC_STATS"] = "允许应用程序读取同步统计信息。"
        descMap["READ_VOICEMAIL"] = "允许应用程序读取系统中的语音邮件。"
        descMap["REBOOT"] = "能够重启设备。"
        descMap["RECEIVE_BOOT_COMPLETED"] = "允许应用程序接收系统完成引导后广播的"
        descMap["RECEIVE_MMS"] = "允许应用程序监控传入的MMS消息。"
        descMap["RECEIVE_SMS"] = "允许应用程序接收SMS消息。"
        descMap["RECEIVE_WAP_PUSH"] = "允许应用程序接收WAP推送消息。"
        descMap["RECORD_AUDIO"] = "允许应用程序录制音频。"
        descMap["REORDER_TASKS"] = "允许应用程序更改任务的Z顺序。"
        descMap["REQUEST_COMPANION_RUN_IN_BACKGROUND"] = "允许随播应用在后台运行。"
        descMap["REQUEST_COMPANION_USE_DATA_IN_BACKGROUND"] = "允许配套应用在后台使用数据。"
        descMap["REQUEST_DELETE_PACKAGES"] = "允许应用程序请求删除包。"
        descMap["REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"] =
            "必须保留应用程序才能使用Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS。这是一个正常的权限：请求它的应用程序将始终被授予权限，而无需用户批准或查看它。"
        descMap["REQUEST_INSTALL_PACKAGES"] = "允许应用程序请求安装包。"
        descMap["REQUEST_INSTALL_PACKAGES"] = "允许应用程序请求安装包。"
        descMap["RESTART_PACKAGES"] = "重启安装包。API级别15弃用"
        descMap["SEND_RESPOND_VIA_MESSAGE"] = "允许应用程序（电话）向其他应用程序发送请求，以处理来电期间的响应消息操作。"
        descMap["SEND_SMS"] = "允许应用程序发送SMS消息。"
        descMap["SET_ALARM"] = "允许应用程序广播Intent以为用户设置警报。"
        descMap["SET_ALWAYS_FINISH"] = "允许应用程序控制在后台放置活动是否立即完成。"
        descMap["SET_ANIMATION_SCALE"] = "修改全局动画缩放系数。"
        descMap["SET_DEBUG_APP"] = "配置应用程序以进行调试。"
        descMap["SET_PREFERRED_APPLICATIONS"] = "API级别15中已弃用。"
        descMap["SET_PROCESS_LIMIT"] = "允许应用程序设置可以运行的最大（不需要）应用程序进程数。"
        descMap["SET_TIME"] = "允许应用程序设置系统时间。"
        descMap["SET_TIME_ZONE"] = "允许应用程序设置系统时区。"
        descMap["SET_WALLPAPER"] = "允许应用程序设置壁纸。"
        descMap["SET_WALLPAPER_HINTS"] = "允许应用程序设置壁纸提示。"
        descMap["SIGNAL_PERSISTENT_PROCESSES"] = "允许应用程序请求将信号发送到所有持久进程。"
        descMap["SMS_FINANCIAL_TRANSACTIONS"] = "允许财务应用读取过滤的短信息。"
        descMap["STATUS_BAR"] = "允许应用程序打开，关闭或禁用状态栏及其图标。"
        descMap["SYSTEM_ALERT_WINDOW"] = "允许应用使用类型创建窗口"
        descMap["TRANSMIT_IR"] = "允许使用设备的红外发射器（如果有）。"
        descMap["UNINSTALL_SHORTCUT"] = "不再支持此权限。"
        descMap["UPDATE_DEVICE_STATS"] = "允许应用程序更新设备统计信息。"
        descMap["USE_BIOMETRIC"] = "允许应用使用设备支持的生物识别模式。"
        descMap["USE_FINGERPRINT"] = "允许应用使用指纹硬件。"
        descMap["USE_FULL_SCREEN_INTENT"] = "屏幕定位"
        descMap["USE_SIP"] = "允许应用程序使用SIP服务。"
        descMap["VIBRATE"] = "允许访问振动器。"
        descMap["WAKE_LOCK"] = "允许使用PowerManager WakeLocks防止处理器休眠或屏幕变暗。"
        descMap["WRITE_APN_SETTINGS"] = "允许应用程序编写apn设置并读取现有apn设置（如用​​户和密码）的敏感字段。"
        descMap["WRITE_CALENDAR"] = "允许应用程序写入用户的日历数据。"
        descMap["WRITE_CALL_LOG"] = "允许应用程序写入（但不读取）用户的呼叫日志数据。"
        descMap["WRITE_CONTACTS"] = "允许应用程序写入用户的联系人数据。"
        descMap["WRITE_EXTERNAL_STORAGE"] = "允许应用程序写入外部存储。"
        descMap["WRITE_GSERVICES"] = "允许应用修改Google服务地图。"
        descMap["WRITE_SECURE_SETTINGS"] = "允许应用程序读取或写入安全系统设置。"
        descMap["WRITE_SETTINGS"] = "允许应用程序读取或写入系统设置。"
        descMap["WRITE_SYNC_SETTINGS"] = "允许应用程序写入同步设置。"
        descMap["WRITE_VOICEMAIL"] = "允许应用程序修改和删除系统中的现有语音邮件。"
        try { // Get the package info
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            // Permissions counter
            var counter = 1
            for (i in packageInfo.requestedPermissions.indices) {
                if (packageInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED != 0) {
                    var permission = packageInfo.requestedPermissions[i]
                    // To make permission name shorter
                    permission = permission.substring(permission.lastIndexOf(".") + 1)
                    if (descMap.containsKey(permission)) {
                        ret.add(Pair(permission, descMap[permission]) as Pair<String, String>)
                    } else {
                        ret.add(Pair(permission, ""))
                    }

                    counter++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ret
    }

}
