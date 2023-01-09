package com.example.medbuddy

data class Treatment(
    var doctorUID: String? = null,
    var patientUID: String? = null,
    var diagnostic: String? = null,
    var medication: String? = null,
    var symptom: String? = null,
    var specialty:String? = null,
    var accepted:Boolean? = null,
    var active:Boolean? = null
)