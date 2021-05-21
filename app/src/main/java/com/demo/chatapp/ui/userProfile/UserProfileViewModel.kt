package com.demo.chatapp.ui.userProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.chatapp.prefs.UserPrefrence
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val prefs: UserPrefrence
) : ViewModel() {

    fun logout(onComplete: () -> Unit) {
        auth.signOut()
        viewModelScope.launch(Dispatchers.IO) {
            prefs.setLoginStatus(false)
            onComplete()
        }
    }
}