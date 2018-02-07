package com.thinkpalm.keepintouch.ui.activities

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thinkpalm.keepintouch.CoreApplication
import com.thinkpalm.keepintouch.FirebaseDatabaseNodes
import com.thinkpalm.keepintouch.R
import com.thinkpalm.keepintouch.model.User
import com.thinkpalm.keepintouch.ui.fragments.ChatFragment
import com.thinkpalm.keepintouch.ui.fragments.MapFragment
import com.thinkpalm.keepintouch.ui.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var mapFragment: MapFragment;
    var mapContainer: FrameLayout? = null
    var otherContainer: FrameLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapContainer = findViewById(R.id.frag_map_container)
        otherContainer = findViewById(R.id.frag_container)
        mapFragment = MapFragment()
        setMapFragment(mapFragment, true)
        mapContainer?.visibility = View.VISIBLE;

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }




    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                if (mapContainer?.visibility == View.VISIBLE) clearBackStack()
                mapContainer?.visibility = View.VISIBLE;
                otherContainer?.visibility = View.GONE;
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                var cleanBackStack = true
                if (mapContainer?.visibility == View.VISIBLE)
                    cleanBackStack = false
                changeFragment(ChatFragment(), cleanBackStack)
                mapContainer?.visibility = View.GONE;
                otherContainer?.visibility = View.VISIBLE;
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                var cleanBackStack = true
                if (mapContainer?.visibility == View.VISIBLE)
                    cleanBackStack = false
                changeFragment(ProfileFragment(), cleanBackStack)
                mapContainer?.visibility = View.GONE;
                otherContainer?.visibility = View.VISIBLE;
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun changeFragment(f: Fragment, cleanStack: Boolean = false) {
        val ft = supportFragmentManager.beginTransaction()
        if (cleanStack) {
            clearBackStack()
        }
        ft.setCustomAnimations(
                R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_popup_enter, R.anim.abc_popup_exit)
        ft.replace(R.id.frag_container, f)
        ft.addToBackStack(null)
        ft.commit()
    }

    fun setMapFragment(f: Fragment, cleanStack: Boolean = false) {
        val ft = supportFragmentManager.beginTransaction()
        if (cleanStack) {
            clearBackStack()
        }
        ft.setCustomAnimations(
                R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_popup_enter, R.anim.abc_popup_exit)
        ft.add(R.id.frag_map_container, f)
        ft.addToBackStack(null)
        ft.commit()
    }

    fun clearBackStack() {
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            val first = manager.getBackStackEntryAt((manager.backStackEntryCount - 1))
            manager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

}
