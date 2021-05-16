package com.demo.chatapp.data.repository

import com.demo.chatapp.data.remote.FirebaseSource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import javax.inject.Inject

class FirebaseRepository @Inject constructor(private val firebaseSrc: FirebaseSource) {

    fun signInWithGoogle(acct: GoogleSignInAccount) =
        firebaseSrc.signInWithGoogle(acct)

}