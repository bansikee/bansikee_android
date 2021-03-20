package com.tomasandfriends.bansikee.src.activities.add_my_plant

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.gun0912.tedpermission.PermissionListener
import com.tomasandfriends.bansikee.R
import com.tomasandfriends.bansikee.databinding.ActivityAddMyPlantBinding
import com.tomasandfriends.bansikee.src.activities.base.BaseActivity
import com.tomasandfriends.bansikee.src.utils.FileUtils.getPathOfImageFileResizing
import com.tomasandfriends.bansikee.src.utils.FileUtils.tedPermissionForPhoto
import com.tomasandfriends.bansikee.src.utils.SystemUtils.showAlert
import java.io.File
import java.util.*

class AddMyPlantActivity : BaseActivity<ActivityAddMyPlantBinding, AddMyPlantViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.activity_add_my_plant
    }

    override fun setViewModel() {
        viewModel = ViewModelProvider(this).get(AddMyPlantViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.getPhotoEvent.observe(this, {
            //get photo from gallery or camera
            tedPermissionForPhoto(this, object: PermissionListener {
                override fun onPermissionGranted() {
                    showChoosingPhotoDialog()
                }

                override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {}
            })
        })

        viewModel.setDateEvent.observe(this, {
            val cal = Calendar.getInstance()
            cal.time = viewModel.startDate.value!!
            DatePickerDialog(this, { _, y, m, d ->
                    val newCal = Calendar.getInstance()
                    newCal.set(y, m, d)
                    viewModel.setStartDate(newCal)
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))
                .show()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val plantIdx = intent.getIntExtra("plantIdx", 0)
        val plantName = intent.getStringExtra("plantName")
        val plantSpecies = intent.getStringExtra("plantSpecies")
        viewModel.getPlantData(plantIdx, plantName!!, plantSpecies!!)
    }

    private fun showChoosingPhotoDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.get_photo_from)
            .setPositiveButton(getString(R.string.choose_from_album)) { _, _ ->

                val albumIntent = Intent(Intent.ACTION_PICK)
                albumIntent.type = MediaStore.Images.Media.CONTENT_TYPE
                resultLauncher.launch(albumIntent)

            }.setNegativeButton(getString(R.string.take_photo_from_cam)) { _, _ ->

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(cameraIntent)

            }.setNeutralButton(getString(R.string.cancel), null)
            .create().show()
    }

    //onActivityResult
    @RequiresApi(Build.VERSION_CODES.R)
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK && result.data != null){

                val photoUri = result.data!!.data
                val imgPath = getPathOfImageFileResizing(this, photoUri!!)

                if (imgPath.isNullOrEmpty())
                    showAlert(this, R.string.app_name, R.string.upload_image_error)
                else{
                    viewModel.setPhotoStrUrl(imgPath)

                    if(photoUri.toString().startsWith("/")){
                        val cacheFile = File(photoUri.path!!)
                        if (cacheFile.exists()) cacheFile.delete()
                    }
                }
            }
        }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.ask_exit_while_adding))
            .setPositiveButton(R.string.yes) {_, _ ->
                finish()
            }.setNegativeButton(R.string.no, null)
            .create().show()
    }
}