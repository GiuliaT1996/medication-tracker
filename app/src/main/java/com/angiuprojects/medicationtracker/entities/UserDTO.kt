package com.angiuprojects.medicationtracker.entities

import java.time.LocalDate

class UserDTO {

    var userID: String = ""
    var profiles: MutableList<Profile> = mutableListOf()
    var settings: Settings = Settings(5)
    var lastUpdate: LocalDate = LocalDate.now()

    constructor()

    constructor(userID: String, profiles: MutableList<Profile>, settings: Settings) {
        this.userID = userID
        this.profiles = profiles
        this.settings = settings
    }
}