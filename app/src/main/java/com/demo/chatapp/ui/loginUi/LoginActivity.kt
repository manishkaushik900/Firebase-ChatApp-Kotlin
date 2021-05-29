package com.demo.chatapp.ui.loginUi

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.demo.chatapp.databinding.ActivityLoginBinding
import com.demo.chatapp.ui.acitivites.MainActivity
import com.demo.chatapp.utils.FirestoreUtil
import com.demo.chatapp.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginFragment"
        private const val RC_SIGN_IN = 9001
    }

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)


        if (FirebaseAuth.getInstance().currentUser != null)
            updateUI()

        setContentView(binding.root)

        sign_in_button.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    }

    override fun onResume() {
        super.onResume()

        sign_in_button.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!

                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)

                viewModel.firebaseAuthWithGoogle(account).observe(this, {
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            progress_bar?.visibility = View.GONE
                            updateUI()
                        }
                        Resource.Status.ERROR -> {
                            progress_bar?.visibility = View.GONE
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }
                        Resource.Status.LOADING -> {
                            progress_bar?.visibility = View.VISIBLE
                        }
                    }
                })
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }


    private fun updateUI() {
        progress_bar?.visibility = View.VISIBLE
        FirestoreUtil.initCurrentUserIfFirstTime {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }
}