package com.tomasandfriends.bansikee.src.activities.plant_details

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tomasandfriends.bansikee.ApplicationClass
import com.tomasandfriends.bansikee.src.SingleLiveEvent
import com.tomasandfriends.bansikee.src.activities.base.BaseViewModel
import com.tomasandfriends.bansikee.src.activities.plant_details.interfaces.PlantDetailsView
import com.tomasandfriends.bansikee.src.activities.plant_details.models.PlantDetailsData
import com.tomasandfriends.bansikee.src.common.interfaces.PlantItemView
import com.tomasandfriends.bansikee.src.common.services.PlantItemService

class PlantDetailsViewModel : BaseViewModel(), PlantDetailsView, PlantItemView {

    private var mPlantIdx = 0
    private var mStatus: String = ""

    private val _plantDetails = MutableLiveData<PlantDetailsData>()
    val plantDetails: LiveData<PlantDetailsData> = _plantDetails

    private val _plantLike = MutableLiveData<Boolean>()
    val plantLike: LiveData<Boolean> = _plantLike

    private val _goAddMyPlantEvent = SingleLiveEvent<Bundle>()
    val goAddMyPlantEvent: LiveData<Bundle> = _goAddMyPlantEvent

    private val plantDetailsService = PlantDetailsService(this)
    private val plantItemService = PlantItemService(this)

    fun getPlantDetails(plantIdx: Int, status: String){
        mPlantIdx = plantIdx
        mStatus = status
        plantDetailsService.getPlantDetails(plantIdx, status)
    }

    fun likeClick(){
        plantItemService.changePlantLike(mPlantIdx)
    }

    override fun getPlantDetailsSuccess(plantDetailsData: PlantDetailsData) {
        _plantDetails.value = plantDetailsData
        _plantLike.value = plantDetailsData.like
    }

    override fun getPlantDetailsFailed(msg: String?) {
        _snackbarMessage.value = msg ?: ApplicationClass.NETWORK_ERROR
    }

    override fun changePlantLikeSuccess(msg: String) {
        plantDetailsService.getPlantDetails(mPlantIdx, mStatus)
    }

    override fun changePlantLikeFailed(msg: String?) {
        _snackbarMessage.value = msg ?: ApplicationClass.NETWORK_ERROR
    }

    fun goAddMyPlantClick(){
        val bundle = Bundle()
        bundle.putInt("plantIdx", mPlantIdx)
        bundle.putString("plantName", plantDetails.value!!.name)
        bundle.putString("plantSpecies", plantDetails.value!!.species)
        _goAddMyPlantEvent.value = bundle
    }
}