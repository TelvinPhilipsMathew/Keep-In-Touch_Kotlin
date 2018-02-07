package com.thinkpalm.keepintouch.model

/**
 * Created by telvin.m on 29-11-2017.
 */
data class User(var userId:String, var name: String,var email:String,var firstName:String,var
lastName:String,var
gender:String,var profilePic:String,var link:String) {
    var uniqueKey: String? =""
    constructor() : this("","","","","","","","")
    var latitude: String=""
    var longitude: String = ""
    var address: String? = ""
}