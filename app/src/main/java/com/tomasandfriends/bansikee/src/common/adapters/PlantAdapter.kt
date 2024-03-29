package com.tomasandfriends.bansikee.src.common.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tomasandfriends.bansikee.databinding.ItemPlantBinding
import com.tomasandfriends.bansikee.databinding.ItemPlantHorizontalBinding
import com.tomasandfriends.bansikee.src.activities.plant_details.PlantDetailsActivity
import com.tomasandfriends.bansikee.src.utils.PlantDiffCallback

class PlantAdapter(context: Context, horizontal: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mContext = context
    private val mHorizontal = horizontal
    private val mDataViewModels = ArrayList<PlantItemViewModel>()
    private lateinit var inflater: LayoutInflater

    lateinit var deleteSearchedPlantListener: DeleteSearchedPlantListener

    interface DeleteSearchedPlantListener {
        fun onDeleteClick(plantIdx: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        inflater = LayoutInflater.from(mContext)

        val itemBinding =
                if(mHorizontal)
                    ItemPlantHorizontalBinding.inflate(inflater, parent, false)
                else
                    ItemPlantBinding.inflate(inflater, parent, false)

        return if (mHorizontal)
            PlantHorizontalViewHolder(mContext, itemBinding as ItemPlantHorizontalBinding)
        else
            PlantViewHolder(mContext, itemBinding as ItemPlantBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val itemViewModel = mDataViewModels[position]

        if (holder is PlantViewHolder) {
            holder.bind(itemViewModel)
        } else if(holder is PlantHorizontalViewHolder){
            holder.bind(itemViewModel)
        }

        itemViewModel.goDetailsEvent.observe(mContext as LifecycleOwner, {
            if(itemViewModel.deleteShowing.value!!) itemViewModel.setDeleteShowing(false)
            else {
                val intent = Intent(mContext, PlantDetailsActivity::class.java)
                intent.putExtra("plantIdx", it)
                intent.putExtra("status", "")
                mContext.startActivity(intent)
            }
        })

        itemViewModel.snackbarMessage.observe(mContext as LifecycleOwner, {
            Snackbar.make(holder.itemView, it, Snackbar.LENGTH_SHORT).show()
        })

        if (itemViewModel.deletable){
            holder.itemView.setOnLongClickListener {
                for (item in mDataViewModels){
                    if (item.deleteShowing.value!!) item.setDeleteShowing(false)
                }
                itemViewModel.setDeleteShowing(true)
                true
            }

            itemViewModel.deleteEvent.observe(mContext as LifecycleOwner, {
                deleteSearchedPlantListener.onDeleteClick(it)
            })
        }
    }

    override fun getItemCount(): Int {
        return mDataViewModels.size
    }

    fun updateItems(items: List<PlantItemViewModel>){
        val diffCallback = PlantDiffCallback(mDataViewModels, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        mDataViewModels.clear()
        mDataViewModels.addAll(items)

        diffResult.dispatchUpdatesTo(this)
    }

    class PlantViewHolder(context: Context, binding: ItemPlantBinding)
        : RecyclerView.ViewHolder(binding.root){
        private val mContext = context
        val mBinding = binding

        fun bind(viewModel: PlantItemViewModel){
            mBinding.viewModel = viewModel
            mBinding.lifecycleOwner = mContext as LifecycleOwner
        }
    }

    class PlantHorizontalViewHolder(context: Context, binding: ItemPlantHorizontalBinding)
        : RecyclerView.ViewHolder(binding.root){
        private val mContext = context
        private val mBinding = binding

        fun bind(viewModel: PlantItemViewModel){
            mBinding.viewModel = viewModel
            mBinding.lifecycleOwner = mContext as LifecycleOwner
        }
    }
}