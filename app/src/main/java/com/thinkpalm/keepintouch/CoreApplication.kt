package com.thinkpalm.keepintouch

import android.app.Application
import com.google.firebase.FirebaseApp
import com.thinkpalm.keepintouch.model.User

/**
 * Created by telvin.m on 28-11-2017.
 */
class CoreApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }

    var mUserList: ArrayList<User?> = ArrayList()
}