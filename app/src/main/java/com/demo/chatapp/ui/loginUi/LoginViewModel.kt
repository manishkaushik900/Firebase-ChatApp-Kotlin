package com.demo.chatapp.ui.loginUi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.chatapp.data.repository.FirebaseRepository
import com.demo.chatapp.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val auth: FirebaseAuth
) : ViewModel() {


    private val gMailUserLiveData = MutableLiveData<Resource<FirebaseUser>>()

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount): LiveData<Resource<FirebaseUser>> {

        repository.signInWithGoogle(account)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser

                    gMailUserLiveData.postValue(Resource.success(user!!))
                } else {
                    // If sign in fails, display a message to the user.
                    gMailUserLiveData.postValue(
                        Resource.error(task.exception?.message.toString(), null)
                    )

                }
            }
        return gMailUserLiveData
    }


}