package com.angiuprojects.medicationtracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.angiuprojects.medicationtracker.R
import com.angiuprojects.medicationtracker.databinding.ActivitySettingsBinding
import com.angiuprojects.medicationtracker.utilities.Constants
import com.angiuprojects.medicationtracker.utilities.ReadWriteJson
import com.google.android.material.snackbar.Snackbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val days = binding.daysAutoComplete
        days.setText(Constants.currentUser.settings.daysInAdvance.toString())

        binding.close.setOnClickListener { finish() }
        binding.save.setOnClickListener { saveSettings() }
    }

    private fun saveSettings() {
        try {
            Constants.currentUser.settings.daysInAdvance =
                binding.daysAutoComplete.text.toString().toInt()

            ReadWriteJson.getInstance().write(this, false)

            finish()
        } catch (e: Exception) {
            Log.e(Constants.logger, "Impossibile salvare! Dati Errati!")
            Snackbar.make(binding.root, "Impossibile salvare! Dati Errati!", Snackbar.LENGTH_LONG)
                .setAction("Azione", null).show()
            return
        }
    }

}