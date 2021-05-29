package com.demo.chatapp.data.model


data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}