package com.demo.chatapp.data.model

import java.util.*


object MessageType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Message {
    val time: Date
    val senderId: String
    val recipientId: String
    val senderName: String
    val type: String
}