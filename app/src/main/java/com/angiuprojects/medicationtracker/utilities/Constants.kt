package com.angiuprojects.medicationtracker.utilities

import com.angiuprojects.medicationtracker.entities.Profile
import com.angiuprojects.medicationtracker.entities.UserDTO

class Constants {

    companion object {

        private lateinit var constantsInstance: Constants

        fun initializeSingleton(): Constants {
            constantsInstance = Constants()
            return constantsInstance
        }

        var logger: String = "Medication Tracker"
        lateinit var currentUser: UserDTO
        var selectedProfile: Profile? = null
    }
}