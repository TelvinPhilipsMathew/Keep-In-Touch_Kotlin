package com.thinkpalm.keepintouch.ui.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.thinkpalm.keepintouch.R
import com.thinkpalm.keepintouch.model.User
import com.thinkpalm.keepintouch.ui.activities.ChatActivity
import com.thinkpalm.keepintouch.ui.activities.MainActivity

/**
 * Created by telvin.m on 29-11-2017.
 */
class UserListAdapter(personList: ArrayList<User?>) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    var userList: ArrayList<User?> = personList
    var mContext: Context? = null
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var model = userList.get(position)
        holder?.name?.text = model?.name
        holder?.place?.text = model?.address
        if (!TextUtils.isEmpty(model?.profilePic)) {
            val finish_target = object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                    holder?.image?.setImageBitmap(bitmap)
                }
            }
            Glide.with(mContext)
                    .load(model?.profilePic)
                    .asBitmap()
                    .override(600, 600)
                    .fitCenter()
                    .placeholder(R.drawable.profile_placeholder)
                    .into(finish_target)
        }
        holder?.itemView?.setOnClickListener(View.OnClickListener {
            val intent = Intent(mContext, ChatActivity::class.java)
            intent.putExtra(ChatActivity.USER_ID,model?.userId)
            intent.putExtra(ChatActivity.USER_NAME,model?.name)
            mContext?.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return userList.size;
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        mContext = parent?.context
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.cell_user, null, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var name = itemView?.findViewById<TextView>(R.id.tv_user_name)
        var place = itemView?.findViewById<TextView>(R.id.place)
        var image = itemView?.findViewById<ImageView>(R.id.user_image)
    }

}