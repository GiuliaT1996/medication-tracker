package com.angiuprojects.medicationtracker.utilities

import android.content.Context
import android.util.Log
import com.angiuprojects.medicationtracker.entities.Settings
import com.angiuprojects.medicationtracker.entities.UserDTO
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.LocalDate
import kotlin.math.floor

class ReadWriteJson {

    companion object {
        private lateinit var readWriteJson: ReadWriteJson

        fun initializeSingleton(): ReadWriteJson {
            readWriteJson = ReadWriteJson()
            return readWriteJson
        }

        fun getInstance(): ReadWriteJson {
            return readWriteJson
        }
    }

    private val fileName = "MedicationTracker.txt"
    private val directory = "Medication Tracker"

    fun getUser(context: Context, resetUserDTO: Boolean) : UserDTO {
        val dir = getDirectory(context)
        val json = read(dir, context)
        Log.i(Constants.logger, "Json : " + json)
        if(json != "" && !resetUserDTO) {
            try {
                return Gson().fromJson(json, UserDTO::class.java)
            } catch (e: Exception) {
                Log.e(Constants.logger, "Errore nella conversione del file in json")
            }
        }
        return createNewUser()
    }

    private fun read(dir: File, context: Context) : String {
        try {
            val file = File(dir, fileName)
            if (!file.exists()) {
                Log.e(Constants.logger, "Il file non esiste")
                write(context, true)
                return ""
            }
            val reader = FileReader(file)
            return reader.readText()
        } catch (e: Exception) {
            Log.e(Constants.logger, "Errore nella conversione del file in json")
        }
        return ""
    }

    private fun createNewUser() : UserDTO {
        val id = "" + floor(Math.random() * 9) + floor(Math.random() * 9) + floor(Math.random() * 9) + floor(Math.random() * 9) + floor(Math.random() * 9) + floor(Math.random() * 9)
        return UserDTO(id, mutableListOf(), Settings())
    }

    fun write(context: Context, newUser: Boolean) {

        val dir = getDirectory(context)
        try {
            val file = File(dir, fileName)
            val writer = FileWriter(file)
            if(!newUser) Constants.currentUser.lastUpdate = LocalDate.now()
            writer.append(if(newUser) "" else Gson().toJson(Constants.currentUser))
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(Constants.logger, "Errore nella scrittura o nel salvataggio del file")
        }
    }

    private fun getDirectory(context: Context) : File {
        val dir = File(context.filesDir, directory)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dir
    }
}