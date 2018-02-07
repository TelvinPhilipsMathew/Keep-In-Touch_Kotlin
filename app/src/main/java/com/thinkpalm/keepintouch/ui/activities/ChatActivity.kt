package com.thinkpalm.keepintouch.ui.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thinkpalm.keepintouch.FirebaseDatabaseNodes
import com.thinkpalm.keepintouch.R
import com.thinkpalm.keepintouch.model.ChatMessage
import com.thinkpalm.keepintouch.ui.adapters.ChatAdapter
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPrefKeys
import com.thinkpalm.keepintouch.utils.sharedPrefHelper.SharedPreferenceHandler
import java.util.*


class ChatActivity : AppCompatActivity() {
    companion object {
        val USER_ID: String = "user_id"
        val USER_NAME: String = "user_name"
    }

    lateinit var rvChatMessage: RecyclerView
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        rvChatMessage = findViewById(R.id.list_of_messages)
        progressbar = findViewById(R.id.progress_bar)
        val linearLayoutManager = LinearLayoutManager(this@ChatActivity)
        linearLayoutManager.stackFromEnd = true
        rvChatMessage.layoutManager = linearLayoutManager
        val userName = intent.extras.getString(USER_NAME)
        val userId = intent.extras.getString(USER_ID)
        val currentUserName = SharedPreferenceHandler.getInstance(this@ChatActivity).getString(SharedPrefKeys.NAME)
        val currentUserId = SharedPreferenceHandler.getInstance(this@ChatActivity).getString(SharedPrefKeys.FACEBOOK_ID)
        var chatKey = ""
        if (currentUserName.compareTo(userName) > 0) {
            chatKey = currentUserName + "_" + currentUserId + "-" + userName + "_" + userId
        } else {

            chatKey = userName + "_" + userId + "-" + currentUserName + "_" + currentUserId
        }
        supportActionBar?.title = userName
        supportActionBar?.setHomeButtonEnabled(true)
        getChatHistory(chatKey)
        handleFabClick(chatKey)
    }

    private fun handleFabClick(chatKey: String) {
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val input = findViewById<EditText>(R.id.input)

                FirebaseDatabase.getInstance()
                        .getReference(FirebaseDatabaseNodes.CHAT)
                        .child(chatKey)
                        .push()
                        .setValue(ChatMessage(input.text.toString(),
                                FirebaseAuth.getInstance()
                                        .currentUser!!
                                        .displayName!!)
                        )

                input.setText("")
            }
        })
    }

    private var chatList: ArrayList<ChatMessage?> = ArrayList()

    private var progressbar: ProgressBar? = null

    private fun getChatHistory(chatKey: String) {
        FirebaseDatabase.getInstance()?.getReference(FirebaseDatabaseNodes.CHAT)?.child(chatKey)?.addValueEventListener(object :
                ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList = ArrayList()
                for (postSnapshot in dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    val message: ChatMessage? = postSnapshot.getValue(ChatMessage::class.java)
                    chatList?.add(message)
                }

                if (chatList?.size > 0) {
                    progressbar?.visibility = View.GONE
                    rvChatMessage?.adapter = ChatAdapter(chatList, this@ChatActivity)
                } else {
                    progressbar?.visibility = View.GONE
                }

            }
        })
    }

}
