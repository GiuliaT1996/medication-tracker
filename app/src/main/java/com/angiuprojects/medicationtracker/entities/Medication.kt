package com.angiuprojects.medicationtracker.entities

class Medication {

    var medicationName: String = ""
    var totalPills: Int = 0
    var pillsADay: Int = 0
    var remainingPills: Int = 0
    var daysList: MutableList<Boolean> = mutableListOf(true, true, true, true, true, true, true)

    constructor()

    constructor(medicationName: String, totalPills: Int, pillsADay: Int, remainingPills: Int, daysList: MutableList<Boolean>) {
        this.medicationName = medicationName
        this.totalPills = totalPills
        this.pillsADay = pillsADay
        this.remainingPills = remainingPills
        this.daysList = daysList
    }

}