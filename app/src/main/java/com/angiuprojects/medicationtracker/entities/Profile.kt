package com.angiuprojects.medicationtracker.entities

class Profile {

    var profileName: String = ""
    var pills: MutableList<Medication> = mutableListOf()

    constructor()

    constructor(profileName: String, pills: MutableList<Medication>) {
        this.profileName = profileName
        this.pills = pills
    }
}