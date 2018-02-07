package com.thinkpalm.keepintouch.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.thinkpalm.keepintouch.CoreApplication
import com.thinkpalm.keepintouch.R
import com.thinkpalm.keepintouch.model.User
import com.thinkpalm.keepintouch.ui.adapters.UserListAdapter
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPrefKeys
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPreferenceHandler


/**
 * Created by telvin.m on 27-11-2017.
 */
class ChatFragment : Fragment() {

    lateinit var rvUserList: RecyclerView
    lateinit var progressbar: ProgressBar
    lateinit var tvEmptyUser: TextView
    var database: FirebaseDatabase? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.chat_fragment, container, false)
        rvUserList = view.findViewById(R.id.rv_chat)
        progressbar = view.findViewById(R.id.progress_bar)
        tvEmptyUser = view.findViewById(R.id.empty_text)
        rvUserList.layoutManager = LinearLayoutManager(context)

        val application: CoreApplication = context.applicationContext as CoreApplication
        var userList = application.mUserList
        if (userList?.size > 0) {
            progressbar.visibility = View.GONE
            tvEmptyUser.visibility = View.GONE
            rvUserList.visibility = View.VISIBLE

            rvUserList.adapter = UserListAdapter(filterUserlist(userList) as ArrayList<User?>);
        } else {
            progressbar.visibility = View.GONE
            tvEmptyUser.visibility = View.VISIBLE
            rvUserList.visibility = View.GONE
        }
        return view
    }

    private fun filterUserlist(userlist: ArrayList<User?>): List<User?> {
        return userlist.filter { user -> !user?.name.equals(SharedPreferenceHandler.getInstance
        (context).getString(SharedPrefKeys.NAME)) }

    }
}