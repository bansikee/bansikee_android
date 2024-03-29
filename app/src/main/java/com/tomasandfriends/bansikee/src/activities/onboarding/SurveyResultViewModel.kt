package com.tomasandfriends.bansikee.src.activities.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tomasandfriends.bansikee.ApplicationClass
import com.tomasandfriends.bansikee.ApplicationClass.Companion.mSharedPreferences
import com.tomasandfriends.bansikee.src.SingleLiveEvent
import com.tomasandfriends.bansikee.src.activities.base.BaseViewModel
import com.tomasandfriends.bansikee.src.common.adapters.PlantAdapter
import com.tomasandfriends.bansikee.src.common.models.PlantData
import com.tomasandfriends.bansikee.src.common.interfaces.PlantsView
import com.tomasandfriends.bansikee.src.common.adapters.PlantItemViewModel
import com.tomasandfriends.bansikee.src.common.services.PlantsService

class SurveyResultViewModel: BaseViewModel(), PlantsView, PlantAdapter.DeleteSearchedPlantListener {

    private val _surveyResultItems = MutableLiveData<List<PlantItemViewModel>>()
    val surveyResultItems: LiveData<List<PlantItemViewModel>> = _surveyResultItems

    private val _surveyResultLoading = MutableLiveData(true)
    val surveyResultLoading: LiveData<Boolean> = _surveyResultLoading

    private val _onboardAgainEvent = SingleLiveEvent<Void?>()
    val onboardAgainEvent: LiveData<Void?> = _onboardAgainEvent

    private val surveyResultService = PlantsService(this)

    val onBoarded = mSharedPreferences!!.getBoolean("onboarded", false)

    init {
        getResult()
    }

    fun getResult(){
        _surveyResultLoading.value = true
        surveyResultService.getRecommendations()
    }

    fun onBoardAgain(){
        _onboardAgainEvent.value = null
    }

    override fun getPlantsSuccess(plants: List<PlantData>) {
        val itemViewModels = ArrayList<PlantItemViewModel>()
        for(plantData in plants){
            itemViewModels.add(PlantItemViewModel(plantData))
        }

        _surveyResultItems.value = itemViewModels
        _surveyResultLoading.value = false
    }

    override fun getPlantsFailed(msg: String?) {
        _surveyResultLoading.value = false
        _snackbarMessage.value = msg ?: ApplicationClass.NETWORK_ERROR
    }

    override fun onDeleteClick(plantIdx: Int) {}
}