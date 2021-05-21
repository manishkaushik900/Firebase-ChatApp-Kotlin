package com.demo.chatapp.utils

import com.demo.chatapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

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

    


}