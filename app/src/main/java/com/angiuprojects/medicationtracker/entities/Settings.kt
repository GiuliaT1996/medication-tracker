package com.angiuprojects.medicationtracker.entities

class Settings {

    var daysInAdvance: Int = 3

    constructor()

    constructor(daysInAdvance: Int) {
        this.daysInAdvance = daysInAdvance
    }
}