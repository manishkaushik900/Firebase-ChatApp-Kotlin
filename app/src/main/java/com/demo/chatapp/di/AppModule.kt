package com.demo.chatapp.di

import android.content.Context
import com.demo.chatapp.R
import com.demo.chatapp.data.remote.FirebaseSource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideGso(@ApplicationContext context: Context): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

    @Provides
    fun provideSigninClient(@ApplicationContext context: Context, gso: GoogleSignInOptions) =
        GoogleSignIn.getClient(context, gso)

    @Provides
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun providesFirebaseSource(auth: FirebaseAuth): FirebaseSource = FirebaseSource(auth)

}