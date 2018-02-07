package com.thinkpalm.keepintouch.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.thinkpalm.keepintouch.R
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPrefKeys
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPreferenceHandler

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if(!SharedPreferenceHandler.getInstance(this@Splash).getBoolean(SharedPrefKeys
                    .IS_LOGIN)) {
                val intent = Intent(this@Splash, LoginActivity::class.java)
                val logo = android.support.v4.util.Pair.create(findViewById<View>(R.id.logo), "logo")
                val title = android.support.v4.util.Pair.create(findViewById<View>(R.id.title), "title")
                val desc = android.support.v4.util.Pair.create(findViewById<View>(R.id.desc), "desc")

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@Splash,
                        logo, title, desc)

                startActivity(intent, options.toBundle())
                finish()
            }else{
                startActivity( Intent(this@Splash, MainActivity::class.java))
                finish()
            }
        }, 3000)
    }
}
