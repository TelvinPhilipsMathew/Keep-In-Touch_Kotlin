package com.thinkpalm.keepintouch.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.thinkpalm.keepintouch.R
import com.thinkpalm.keepintouch.model.ChatMessage
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPrefKeys
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPreferenceHandler
import java.text.SimpleDateFormat
import java.util.*
import android.R.attr.gravity
import android.widget.FrameLayout



/**
 * Created by telvin.m on 04-12-2017.
 */
class ChatAdapter(chatList: ArrayList<ChatMessage?>, val context: Context) : RecyclerView
.Adapter<ChatAdapter.ViewHolder>() {
    var mChatList: ArrayList<ChatMessage?> = chatList
    var mContext: Context = context
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var model = mChatList.get(position)
        holder?.name?.text = model?.messageUser
        holder?.text?.text = model?.messageText
        if(model?.messageTime!=null) {
            holder?.time?.text = getDate(model?.messageTime)
            holder?.time?.visibility = View.GONE
        }else
            holder?.time?.visibility = View.GONE
        val currentUserName: String = SharedPreferenceHandler.getInstance(mContext).getString(SharedPrefKeys.NAME);
        if(currentUserName.equals(model?.messageUser)){
            holder?.llParent?.gravity=Gravity.RIGHT
            val params = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                    .LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.TOP or Gravity.RIGHT
            holder?.llParent?.layoutParams=params
        }else{
            holder?.llParent?.gravity=Gravity.LEFT
            val params = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                    .LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.TOP or Gravity.LEFT
            holder?.llParent?.layoutParams=params
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    fun getDate(milliseconds: Long): String {
        val dateFormat = SimpleDateFormat("MMMM dd", Locale.ENGLISH)
        return dateFormat.format(Date(milliseconds))
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.cell_chat, null, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var name = itemView?.findViewById<TextView>(R.id.tv_user_name)
        var text = itemView?.findViewById<TextView>(R.id.text)
        var time = itemView?.findViewById<TextView>(R.id.time)
        var llParent = itemView?.findViewById<LinearLayout>(R.id.parent_ll)
    }

    fun setData(chatList: java.util.ArrayList<ChatMessage?>) {
        mChatList = chatList
        notifyDataSetChanged()
    }

}