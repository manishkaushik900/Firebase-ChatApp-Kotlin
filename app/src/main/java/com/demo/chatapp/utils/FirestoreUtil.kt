package com.demo.chatapp.utils

import android.content.Context
import android.util.Log
import com.demo.chatapp.data.model.*
import com.demo.chatapp.recyclerviewItem.ImageMessageItem
import com.demo.chatapp.recyclerviewItem.TextMessageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item

object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${
                FirebaseAuth.getInstance().uid ?: throw  NullPointerException(
                    "UUid is null"
                )
            }"
        )

    /*Create a new user*/
    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            if (!it.exists()) {
                val newUser =
                    User(
                        FirebaseAuth.getInstance().currentUser?.displayName ?: "", "", null,
                        mutableListOf()
                    )

                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }

            } else {
                onComplete()
            }

        }

    }

    /*update current user profile*/
    fun updateCurrentUser(name: String = "", bio: String = "", profilePicturePath: String? = null) {

        val fieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) fieldMap["name"] = name
        if (bio.isNotBlank()) fieldMap["bio"] = bio
        if (profilePicturePath != null)
            fieldMap["profilePicturePath"] = profilePicturePath

        currentUserDocRef.update(fieldMap)
    }

    /*get current user*/
    fun getCurrentUser(onComplete: (user: User) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }

    /*get FCM token*/
    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)!!
            onComplete(user.registrationTokens)
        }
    }

    /*Set FCM token*/
    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        currentUserDocRef.update(mapOf("registrationToken" to registrationTokens))
    }

    fun addUsersListener(context: Context, onListen: (List<DocumentSnapshot>) -> Unit): ListenerRegistration {
        return firestoreInstance.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Users listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<DocumentSnapshot>()
                querySnapshot!!.documents.forEach {
                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                        items.add(it/*.toObject(User::class.java)!!*/)
                }
                onListen(items)
            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun getOrCreateChatChannel(
        otherUserId: String,
        onComplete: (channelId: String) -> Unit
    ) {
        currentUserDocRef.collection("engagedChatChannels")
            .document(otherUserId).get().addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }

                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelsCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                currentUserDocRef
                    .collection("engagedChatChannels")
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                firestoreInstance.collection("users").document(otherUserId)
                    .collection("engagedChatChannels")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }
    }

    fun addChatMessagesListener(
        channelId: String, context: Context,
        onListen: (List<Item>) -> Unit
    ): ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == MessageType.TEXT)
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    else
                        items.add(
                            ImageMessageItem(
                                it.toObject(ImageMessage::class.java)!!,
                                context
                            )
                        )
                    return@forEach
                }
                onListen(items)
            }
    }

    fun sendMessage(message: Message, channelId: String) {
        chatChannelsCollectionRef.document(channelId)
            .collection("messages")
            .add(message)
    }

}