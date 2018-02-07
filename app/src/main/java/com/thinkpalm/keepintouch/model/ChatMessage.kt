package com.thinkpalm.keepintouch.model

import java.util.*


/**
 * Created by telvin.m on 30-11-2017.
 */
class ChatMessage {

    var messageText: String? = null
    var messageUser: String? = null
    var messageTime: Long = 0

    constructor(messageText: String, messageUser: String) {
        this.messageText = messageText
        this.messageUser = messageUser

        // Initialize to current time
        messageTime = Date().getTime()
    }

    constructor() {

    }
}