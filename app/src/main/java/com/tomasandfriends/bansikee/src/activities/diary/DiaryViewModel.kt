package com.tomasandfriends.bansikee.src.activities.diary

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tomasandfriends.bansikee.ApplicationClass
import com.tomasandfriends.bansikee.ApplicationClass.Companion.mSimpleDateFormat
import com.tomasandfriends.bansikee.src.activities.base.BaseViewModel
import com.tomasandfriends.bansikee.src.activities.diary.interfaces.DiaryView
import com.tomasandfriends.bansikee.src.activities.diary.models.DiaryDetailsData
import com.tomasandfriends.bansikee.src.utils.SystemUtils.getDayOfWeek
import java.util.*
import kotlin.collections.ArrayList

class DiaryViewModel: BaseViewModel(), DiaryView {
    private var diaryIdx = 0
    private var myPlantIdx = 0

    private val _diaryDate = MutableLiveData<String>()
    val diaryDate: LiveData<String> = _diaryDate

    private val _diaryImages = MutableLiveData<ArrayList<String>>()
    val diaryImages: LiveData<ArrayList<String>> = _diaryImages

    private val _dDay = MutableLiveData<Int>()
    val dDay: LiveData<Int> = _dDay

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> = _nickname

    private val _weather = MutableLiveData("GOOD")
    val weather: LiveData<String> = _weather

    private val _height = MutableLiveData<Int>()
    val height: LiveData<Int> = _height

    val watered = MutableLiveData(false)

    val diaryContents = MutableLiveData<String>()

    private val diaryService = DiaryService(this)

    fun initWriteDiary(bundle: Bundle){
        val today = Calendar.getInstance()
        val dateToday = today.time
        _diaryDate.value = "${mSimpleDateFormat.format(dateToday)} ${getDayOfWeek(today.get(Calendar.DAY_OF_WEEK))}"

        _dDay.value = bundle.getInt("dDay")+1
        _nickname.value = bundle.getString("myPlantName")
        myPlantIdx = bundle.getInt("myPlantIdx")
    }

    fun getDiaryData(diaryIdx: Int){
        this.diaryIdx = diaryIdx
        diaryService.getDiaryDetails(diaryIdx)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getDiaryDetailsSuccess(diaryDetails: DiaryDetailsData) {
        diaryContents.value = diaryDetails.contents
        _diaryImages.value = diaryDetails.diaryPictures.imgUrls
        _height.value = diaryDetails.height
        _nickname.value = diaryDetails.nickname
        _weather.value = diaryDetails.weather
        watered.value = diaryDetails.watered == "YES"
        _diaryDate.value = diaryDetails.getStrDate()
    }

    override fun getDiaryDetailsFailed(msg: String?) {
        _snackbarMessage.value = msg ?: ApplicationClass.NETWORK_ERROR
    }
}