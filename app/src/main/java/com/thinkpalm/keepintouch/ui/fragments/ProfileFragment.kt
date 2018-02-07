package com.thinkpalm.keepintouch.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.thinkpalm.keepintouch.R
import com.thinkpalm.keepintouch.ui.activities.LoginActivity
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPrefKeys
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPreferenceHandler

/**
 * Created by telvin.m on 27-11-2017.
 */
class ProfileFragment : Fragment() {
    var profileImage: ImageView? = null;
    var btnLogout: ImageView? = null;
    var name: TextView? = null;
    var mEmail: TextView? = null;
    var mGender: TextView? = null;
    var mFullname: TextView? = null;
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.profile_fragment, container, false)
        initUI(view)
        btnLogout?.setOnClickListener(View.OnClickListener {
            SharedPreferenceHandler.getInstance(context).saveBoolean(
                    SharedPrefKeys.IS_LOGIN, false)

            startActivity(Intent(context, LoginActivity::class.java))
            (context as Activity).finish()
        })
        setUI()
        return view
    }

    private fun initUI(view: View) {
        profileImage = view.findViewById<ImageView>(R.id.profile_pic)
        btnLogout = view.findViewById<ImageView>(R.id.logout)
        name = view.findViewById<TextView>(R.id.name)
        mEmail = view.findViewById<TextView>(R.id.tv_email)
        mFullname = view.findViewById<TextView>(R.id.tv_fullname)
        mGender = view.findViewById<TextView>(R.id.tv_gender)
    }

    private fun setUI() {
        val instance = SharedPreferenceHandler.getInstance(context)

        if (!TextUtils.isEmpty(instance.getString(SharedPrefKeys
                .NAME))) {
            name?.text = instance.getString(SharedPrefKeys
                    .NAME)
        } else if (!TextUtils.isEmpty(instance.getString(SharedPrefKeys
                .EMAIL))) {
            name?.text = instance.getString(SharedPrefKeys
                    .EMAIL)
            mEmail?.text = instance.getString(SharedPrefKeys
                    .EMAIL)
        }
        if (!TextUtils.isEmpty(instance.getString(SharedPrefKeys
                .GENDER))) {
            mGender?.text = instance.getString(SharedPrefKeys
                    .GENDER)
        }
        var fullname = "";
        if (!TextUtils.isEmpty(instance.getString(SharedPrefKeys
                .FIRSTNAME))) {
            fullname = instance.getString(SharedPrefKeys
                    .FIRSTNAME);
            if (!TextUtils.isEmpty(instance.getString(SharedPrefKeys
                    .LASTNAME))) {
                fullname += " " + instance.getString(SharedPrefKeys
                        .LASTNAME)
            }
            mFullname?.text = fullname
        }
        if (!TextUtils.isEmpty(instance.getString(SharedPrefKeys.PROFILE_PICTURE))) {
            val finish_target = object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                    profileImage?.setImageBitmap(bitmap)
                }
            }
            Glide.with(context)
                    .load(instance.getString(SharedPrefKeys.PROFILE_PICTURE))
                    .asBitmap()
                    .override(600, 600)
                    .fitCenter()
                    .placeholder(R.drawable.profile_placeholder)
                    .into(finish_target)
        }
    }
}