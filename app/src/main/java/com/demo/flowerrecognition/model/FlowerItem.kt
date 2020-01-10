package com.demo.flowerrecognition.model

import android.os.Parcelable
import com.demo.flowerrecognition.util.PinyinUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

//{"Ind": 0, "Name_En": "Chrysanthemum", "Name_Ch": "菊花", "Name_Sci": "Chrysanthemum morifolium", "Language": "清净高洁", "Med": "", "Literary": "你是末日前最后一汪清泉，我久久驻足，不敢向前。", "Description": "多年生草本，高60-150厘米。茎直立，分枝或不分枝，被柔毛。叶卵形至披针形，长5-15厘米，羽状浅裂或半裂，有短柄，叶下面被白色短柔毛。头状花序直径2.5-20厘米，大小不一。总苞片多层，外层外面被柔毛。舌状花颜色各种。管状花黄色。", "Genus_Ch": "筒蒿属", "Family_Ch": "菊科"}
@Parcelize
data class FlowerItem(
    @SerializedName("Ind")
    val ind: Long,

    @SerializedName("Name_En")
    val nameEn: String,

    @SerializedName("Name_Ch")
    val nameCh: String,

    @SerializedName("Name_Sci")
    val nameSci: String,

    @SerializedName("Language")
    val language: String,

    @SerializedName("Med")
    val med: String,

    @SerializedName("Literary")
    val literary: String,

    @SerializedName("Description")
    val description: String,

    @SerializedName("Genus_Ch")
    val genusCh: String,

    @SerializedName("Family_Ch")
    val familyCh: String
) : Parcelable {
    @IgnoredOnParcel
    private var pinyin: String? = null
    fun getPinyin(): String? {
        if (pinyin == null) {
            this.pinyin = PinyinUtils.instance.getSelling(nameCh)
        }
        return pinyin
    }


}
