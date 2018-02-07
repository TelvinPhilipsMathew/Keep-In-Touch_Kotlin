package com.thinkpalm.keepintouch.ui.fragments

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thinkpalm.keepintouch.CoreApplication
import com.thinkpalm.keepintouch.FirebaseDatabaseNodes
import com.thinkpalm.keepintouch.R
import com.thinkpalm.keepintouch.model.User
import com.thinkpalm.keepintouch.ui.activities.ChatActivity
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPrefKeys
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPreferenceHandler


/**
 * Created by telvin.m on 27-11-2017.
 */
class MapFragment : Fragment(), OnMapReadyCallback {


    override fun onMapReady(googleMap: GoogleMap?) {

        map = googleMap
        getUserList()
    }

    private fun getUserList() {


        FirebaseDatabase.getInstance()?.getReference(FirebaseDatabaseNodes.USERS)?.addValueEventListener(object :
                ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val personList = java.util.ArrayList<User?>()
                for (postSnapshot in dataSnapshot.getChildren()) {
                    val person = postSnapshot.getValue(User::class.java)
                    person?.uniqueKey = postSnapshot.key;
                    personList.add(person)
                }
                try {
                    (context?.applicationContext as CoreApplication).mUserList = personList
                    setUpMap()
                } catch (e: Exception) {

                }
            }
        })
    }

    private fun setUpMap() {

        val mUserList = (context.applicationContext as CoreApplication).mUserList
        val sharedPreferenceHandler = SharedPreferenceHandler.getInstance(context)
        var myLocation = LatLng(10.009018, 76.361631)
        if (!TextUtils.isEmpty(sharedPreferenceHandler.getString(SharedPrefKeys.LATITUDE)) && !TextUtils.isEmpty(sharedPreferenceHandler.getString(SharedPrefKeys.LONGTITUDE)))
            myLocation = LatLng(sharedPreferenceHandler.getString(SharedPrefKeys.LATITUDE).toDouble(), sharedPreferenceHandler.getString(SharedPrefKeys.LONGTITUDE).toDouble())

        map!!.addMarker(MarkerOptions().position(myLocation)
                .title(sharedPreferenceHandler.getString(SharedPrefKeys.NAME)))

        for (user in mUserList) {
            if (!TextUtils.isEmpty(user?.latitude) && !TextUtils.isEmpty(user?.longitude)) {
                val conf = Bitmap.Config.ARGB_8888
                val bmp = Bitmap.createBitmap(100, 100, conf)
                val canvas1 = Canvas(bmp)

                val color = Paint()
                color.setTextSize(35f)
                color.setColor(Color.BLACK)
                val loc = LatLng(user?.latitude!!.toDouble(), user?.longitude!!.toDouble())

                canvas1.drawBitmap(BitmapFactory.decodeResource(resources,
                        R.mipmap.ic_launcher), 0f, 0f, color)
                canvas1.drawText(user?.name, 30f, 40f, color)



                val customMarkerView = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as
                        LayoutInflater).inflate(R.layout.view_custom_marker, null)
                val markerImageView = customMarkerView.findViewById<ImageView>(R.id.profile_image) as
                        ImageView
                if (!TextUtils.isEmpty(user.profilePic)) {
                    val finish_target = object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(bitmap: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                            markerImageView.setImageBitmap(bitmap)
                            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight())
                            customMarkerView.buildDrawingCache()
                            val returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                                    Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(returnedBitmap)
                            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
                            val drawable = customMarkerView.getBackground()
                            if (drawable != null)
                                drawable!!.draw(canvas)
                            customMarkerView.draw(canvas)
                            map!!.addMarker(MarkerOptions()
                                    .position(loc)!!
                                    .title(user?.name)
                                    .snippet("Click to chat")
                                    .icon(BitmapDescriptorFactory.fromBitmap(returnedBitmap)))
                        map?.setOnInfoWindowClickListener {
                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra(ChatActivity.USER_ID,user?.userId)
                            intent.putExtra(ChatActivity.USER_NAME,user?.name)
                            startActivity(intent)
                        }
                        }
                    }
                    Glide.with(context)
                            .load(user.profilePic)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .thumbnail(0.1f)
                            .into(finish_target)

                }else {
                    customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight())
                    customMarkerView.buildDrawingCache()
                    val returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                            Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(returnedBitmap)
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
                    val drawable = customMarkerView.getBackground()
                    if (drawable != null)
                        drawable!!.draw(canvas)
                    customMarkerView.draw(canvas)
                    map!!.addMarker(MarkerOptions()
                            .position(loc)!!
                            .title(user?.name)
                            .snippet("Click to chat")
                            .icon(BitmapDescriptorFactory.fromBitmap(returnedBitmap)))
                }
            }
        }

        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
        map!!.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)

    }


private fun setUpMapIfNeeded() {
    if (map == null) {
        val mapFrag = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag.getMapAsync(this)
    }
}


private var map: GoogleMap? = null

override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                          savedInstanceState: Bundle?): View? {
    val v = inflater!!.inflate(R.layout.map_fragment, null, false)
    setUpMapIfNeeded()
    return v
}
}