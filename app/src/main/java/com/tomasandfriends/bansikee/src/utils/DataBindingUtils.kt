package com.tomasandfriends.bansikee.src.utils

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.Gravity.CENTER
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputLayout
import com.tomasandfriends.bansikee.R
import com.tomasandfriends.bansikee.src.activities.main.fragment_encyclopedia.EncyclopediaViewModel
import com.tomasandfriends.bansikee.src.activities.onboarding.OnboardingViewModel
import com.tomasandfriends.bansikee.src.activities.onboarding.models.SurveyData
import com.tomasandfriends.bansikee.src.activities.sign_up.SignUpViewModel
import com.tomasandfriends.bansikee.src.common.adapters.*
import kotlin.math.roundToInt

object DataBindingUtils {

    //add fade in/out animation
    @BindingAdapter("fadeVisibility")
    @JvmStatic
    fun setFadeVisibility(view : View, visible : Boolean){
        val animId =
            if (visible) R.anim.fade_in
            else R.anim.fade_out

        val animFade = AnimationUtils.loadAnimation(view.context, animId)
        view.animation = animFade
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    //control editText background
    @BindingAdapter("backgroundReactive")
    @JvmStatic
    fun setBackgroundReactive(textInputLayout: TextInputLayout, errMsg : Int?){
        val editText = textInputLayout.editText
        val context = textInputLayout.context

        editText!!.setOnFocusChangeListener { v, hasFocus ->
            v.background =
                    if (errMsg != null && errMsg != 0) {
                        textInputLayout.error = context.getString(errMsg)
                        ContextCompat.getDrawable(context, R.drawable.edittext_background_error)
                    } else {
                        if (hasFocus)
                            ContextCompat.getDrawable(context, R.drawable.edittext_background_focus)
                        else
                            ContextCompat.getDrawable(context, R.drawable.edittext_background)
                    }
        }

        if (errMsg != null && errMsg != 0) {
            textInputLayout.error = context.getString(errMsg)
            editText.background = ContextCompat.getDrawable(context, R.drawable.edittext_background_error)
        } else {
            textInputLayout.error = null
            editText.background =
                    if (editText.isFocused)
                        ContextCompat.getDrawable(context, R.drawable.edittext_background_focus)
                    else
                        ContextCompat.getDrawable(context, R.drawable.edittext_background)
        }
    }

    @BindingAdapter("nicknameDuplicated", "resultMessage" ,"viewModel")
    @JvmStatic
    fun setCheckNickname(textInputLayout: TextInputLayout, duplicated: Boolean,
                         msg: Int?, viewModel: SignUpViewModel){
        val editText = textInputLayout.editText
        val context = textInputLayout.context

        editText!!.setOnFocusChangeListener{ v, hasFocus ->
            v.background =
                    if (msg != null && duplicated) {
                        textInputLayout.error = context.getString(msg)
                        ContextCompat.getDrawable(context, R.drawable.edittext_background_error)
                    } else {
                        if (hasFocus)
                            ContextCompat.getDrawable(context, R.drawable.edittext_background_focus)
                        else
                            ContextCompat.getDrawable(context, R.drawable.edittext_background)
                    }

            if(!hasFocus)
                viewModel.checkNickname()
        }

        if (msg != null && duplicated && editText.text.isNotEmpty()){
            textInputLayout.helperText = null
            textInputLayout.error = context.getText(msg)
            editText.background = ContextCompat.getDrawable(context, R.drawable.edittext_background_error)
        } else{
            if(msg != null) textInputLayout.helperText = context.getText(msg)
            textInputLayout.error = null
            editText.background =
                    if (editText.isFocused)
                        ContextCompat.getDrawable(context, R.drawable.edittext_background_focus)
                    else
                        ContextCompat.getDrawable(context, R.drawable.edittext_background)
        }
    }

    //set next button on SurveyFragment
    @BindingAdapter("enableNext")
    @JvmStatic
    fun setEnableAndBgColor(view: TextView, activeIndex: LiveData<Int>){

        activeIndex.observe(view.context as LifecycleOwner, {
            view.isEnabled = it >= 0
            view.background =
                if(it >= 0)
                    ContextCompat.getDrawable(view.context, R.drawable.btn_round_green_dark)
                else
                    ContextCompat.getDrawable(view.context, R.drawable.btn_round_green_dark_transparent)
        })
    }

    //set answer buttons on SurveyFragment
    @BindingAdapter("answers", "viewModel")
    @JvmStatic
    fun setAnswers(parent: LinearLayout, surveyData: SurveyData, viewModel: OnboardingViewModel){
        val options = surveyData.options
        val context = parent.context
        val dm = context.resources.displayMetrics

        parent.removeAllViews()
        for (i in options.indices){

            val tvAnswer = TextView(parent.context)
            tvAnswer.text = options[i].content
            tvAnswer.gravity = CENTER

            if(options[i].optionIdx == surveyData.selectedIdx.value){
                tvAnswer.setTextColor(context.getColor(R.color.white))
                tvAnswer.background = ContextCompat.getDrawable(context, R.drawable.btn_round_green)
            } else {
                tvAnswer.setTextColor(context.getColor(R.color.text_gray))
                tvAnswer.background = ContextCompat.getDrawable(context, R.drawable.btn_round_transparent)
            }

            val typeface = Typeface.DEFAULT_BOLD
            tvAnswer.typeface = typeface

            val paddingSize = (25 * dm.density).roundToInt()
            tvAnswer.setPadding(0, paddingSize, 0, paddingSize)

            val params = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            val marginSize = (8 * dm.density).roundToInt()
            params.topMargin = marginSize
            params.bottomMargin = marginSize
            tvAnswer.layoutParams = params

            tvAnswer.setOnClickListener {
                viewModel.onOptionClick(options[i].optionIdx)

                for(j in options.indices){
                    val tv = parent.getChildAt(j) as TextView

                    if(options[j].optionIdx == surveyData.selectedIdx.value){
                        tv.setTextColor(context.getColor(R.color.white))
                        tv.background = ContextCompat.getDrawable(context, R.drawable.btn_round_green)
                    } else {
                        tv.setTextColor(context.getColor(R.color.text_gray))
                        tv.background = ContextCompat.getDrawable(context, R.drawable.btn_round_transparent)
                    }
                }
            }

            parent.addView(tvAnswer)
        }
    }

    //setImage
    @BindingAdapter("imgUrl")
    @JvmStatic
    fun setImageUrl(view: ImageView, imgUrl: String?){
        Glide.with(view.context).load(imgUrl).into(view)
    }

    @BindingAdapter("roundImgUrl")
    @JvmStatic
    fun setRoundImageUrl(view: ImageView, imgUrl: String?){
        Glide.with(view.context).load(imgUrl)
                .circleCrop()
                .placeholder(ContextCompat.getDrawable(view.context, R.drawable.image_background))
                .into(view)
    }

    //set PlantAdapter
    @BindingAdapter("plantItems")
    @JvmStatic
    fun setPlantAdapter(view: RecyclerView, itemViewModels: LiveData<List<PlantItemViewModel>>){
        if (view.adapter == null){
            val plantAdapter = PlantAdapter(view.context,
                    (view.layoutManager as LinearLayoutManager).orientation == LinearLayout.HORIZONTAL)
            view.adapter = plantAdapter
        }

        if(itemViewModels.value != null)
                (view.adapter as PlantAdapter).updateItems(itemViewModels.value!!)
    }

    //set PagingPlantAdapter
    @BindingAdapter("pagingPlantItems", "viewModel", "refreshing")
    @JvmStatic
    fun setPagingPlantAdapter(view: RecyclerView,
                              itemViewModels: LiveData<List<PlantItemViewModel>>,
                              viewModel: EncyclopediaViewModel,
                              refreshing: LiveData<Boolean>){

        if (view.adapter == null){
            val pagingPlantAdapter = PagingPlantAdapter(view.context)
            view.adapter = pagingPlantAdapter
        }

        val mAdapter = view.adapter as PagingPlantAdapter

        if (itemViewModels.value != null) {
            if (itemViewModels.value!!.size == mAdapter.itemCount-1 && !refreshing.value!!){

                mAdapter.setLastPage(true)
                view.clearOnScrollListeners()

            } else {

                mAdapter.setLastPage(false)
                mAdapter.updateItems(itemViewModels.value!!)

                view.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val lastVisiblePosition =
                                (view.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        if (lastVisiblePosition == mAdapter.itemCount-1){
                            viewModel.onLoadMore()
                        }
                    }
                })
            }
        }
    }

    //set MyPlantAdapter
    @BindingAdapter("myPlantItems")
    @JvmStatic
    fun setMyPlantAdapter(view: RecyclerView, itemViewModels: LiveData<List<MyPlantItemViewModel>>){
        if (view.adapter == null){
            val myPlantAdapter = MyPlantAdapter(view.context)
            view.adapter = myPlantAdapter
        }

        if(itemViewModels.value != null)
                (view.adapter as MyPlantAdapter).updateItems(itemViewModels.value!!)
    }

    //set DiaryAdapter
    @BindingAdapter("diaryItems")
    @JvmStatic
    fun setDiaryAdapter(view: RecyclerView, itemViewModels: LiveData<List<DiaryItemViewModel>>){
        if (view.adapter == null){
            val diaryAdapter = DiaryAdapter(view.context,
                    (view.layoutManager as LinearLayoutManager).orientation == LinearLayout.HORIZONTAL)
            view.adapter = diaryAdapter
        }

        if(itemViewModels.value != null)
            (view.adapter as DiaryAdapter).updateItems(itemViewModels.value!!)
    }

    //searching on Encyclopedia
    @BindingAdapter("searchingViewModel")
    @JvmStatic
    fun setSearching(view: EditText, viewModel: EncyclopediaViewModel){
        view.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                viewModel.searchPlantsClick()
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }
    }

    //set animation for delete btn in MyPlantItem
    @BindingAdapter("showing")
    @JvmStatic
    fun setShowingAnimation(view: View, showing: Boolean){
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.left_fade_in)
        view.visibility = if (showing) View.VISIBLE else View.GONE

        if(showing) view.startAnimation(animation)
    }

    @BindingAdapter("shaking")
    @JvmStatic
    fun setShakingAnimation(view: View, shaking: Boolean){
        val shakeAnim = AnimationUtils.loadAnimation(view.context, R.anim.shake)
        if (shaking){
            view.startAnimation(shakeAnim)
        }
    }
}