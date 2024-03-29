package com.tomasandfriends.bansikee.src.activities.diary.models

import com.google.gson.annotations.SerializedName
import com.tomasandfriends.bansikee.ApplicationClass
import com.tomasandfriends.bansikee.src.common.models.DefaultResponse
import com.tomasandfriends.bansikee.src.utils.SystemUtils.getDayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryDetailsResponse: DefaultResponse() {

    @SerializedName("data")
    val diaryData = DiaryDetailsData()

}

class DiaryDetailsData {

    @SerializedName("contents")
    val contents = ""

    @SerializedName("diaryPictures")
    val diaryPictures = ArrayList<String>()

    @SerializedName("dayFromBirth")
    val dayFromStart = 0

    @SerializedName("height")
    val height = 0

    @SerializedName("myDiaryId")
    val diaryId = 0

    @SerializedName("nickName")
    val nickname = ""

    @SerializedName("watered")
    val watered = ""

    @SerializedName("weather")
    val weather = ""

    @SerializedName("writeDate")
    val writeDate = ""

    fun getStrDate(): String{
        var result = ""

        val localDate = LocalDateTime.parse(writeDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        result = "${localDate.format(ApplicationClass.localDateTimeFormat)} ${getDayOfWeek(if(localDate.dayOfWeek.value == 7) 1 else localDate.dayOfWeek.value+1)}"

        return result
    }
}

class AddDiaryBody(private val myPlantId: Int,
                   private val dailyPictures: ArrayList<String>,
                   private val weather: String,
                   private val height: Int,
                   private val watered: String,
                   private val contents: String)