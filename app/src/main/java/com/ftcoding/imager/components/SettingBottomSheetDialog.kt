package com.ftcoding.imager.components

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.ftcoding.imager.R
import com.ftcoding.imager.databinding.GetPhotoBottomSheetBinding
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.repository.prefstore.PrefsStoreImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


object BottomSheet {

    private lateinit var prefsStore: PrefsStore
    suspend fun showFloatingSheetDialog(
        context: Context,
        inflater: LayoutInflater
    ) {

        // setting floating Dialog
        val floatingSheetDialog = Dialog(context)
        val bindingSheet = DataBindingUtil.inflate<GetPhotoBottomSheetBinding>(
            inflater, R.layout.get_photo_bottom_sheet, null, false
        )

        // prefStore initialization
        prefsStore = PrefsStoreImpl(context)

        floatingSheetDialog.setContentView(bindingSheet.root)

        bindingSheet.apply {
            spImgQualitySetting.adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                ImageQualitySetting.values()
            )
            spProfileImgQualitySetting.adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                ImageQualitySetting.values()
            )

            // get saved prefsStore data
            val getSavedImgQuality = prefsStore.getImageQuality.first()
            val getSavedProfileImgQuality = prefsStore.getProfileImageQuality.first()

            // set the saved imager_logo.png quality to spinner
            when (getSavedImgQuality) {
                ImageQualitySetting.HIGH.name -> { spImgQualitySetting.setSelection(0) }
                ImageQualitySetting.MEDIUM.name -> { spImgQualitySetting.setSelection(1) }
                ImageQualitySetting.LOW.name -> { spImgQualitySetting.setSelection(2) }
            }
            when (getSavedProfileImgQuality) {
                ImageQualitySetting.HIGH.name -> { spProfileImgQualitySetting.setSelection(0) }
                ImageQualitySetting.MEDIUM.name -> { spProfileImgQualitySetting.setSelection(1) }
                ImageQualitySetting.LOW.name -> { spProfileImgQualitySetting.setSelection(2) }
            }

            // check theme
            when (context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_NO -> settingThemeSwitch.isChecked = false
                Configuration.UI_MODE_NIGHT_YES -> settingThemeSwitch.isChecked = true
                Configuration.UI_MODE_NIGHT_UNDEFINED -> settingThemeSwitch.isChecked = false
            }

            // click listener on save button
            btSaveSetting.setOnClickListener {
                val imgQuality = spImgQualitySetting.selectedItem.toString()
                val profileImgQuality = spProfileImgQualitySetting.selectedItem.toString()

                // save imager_logo.png in prefsDataStore
                runBlocking {
                    prefsStore.saveImageQuality(imgQuality)
                    prefsStore.saveProfileImageQuality(profileImgQuality)
                }

                // change theme
                if (settingThemeSwitch.isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                // cancel the Dialog
                floatingSheetDialog.cancel()
                floatingSheetDialog.dismiss()
            }

            // click listener on cancel button and close dialog
            btCancelSetting.setOnClickListener {
                floatingSheetDialog.cancel()
                floatingSheetDialog.dismiss()
            }
        }
        floatingSheetDialog.show()
    }

}


