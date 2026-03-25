package com.ferrytech.n_droid.data.repository

import com.ferrytech.n_droid.data.model.AuthProvider
import com.ferrytech.n_droid.data.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email,
            displayName = firebaseUser.displayName,
            phoneNumber = firebaseUser.phoneNumber,
            photoUrl = firebaseUser.photoUrl?.toString(),
            provider = getAuthProvider(firebaseUser)
        )
    }

    private fun getAuthProvider(user: FirebaseUser): AuthProvider {
        return when {
            user.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } -> AuthProvider.GOOGLE
            user.providerData.any { it.providerId == PhoneAuthProvider.PROVIDER_ID } -> AuthProvider.PHONE
            user.providerData.any { it.providerId == EmailAuthProvider.PROVIDER_ID } -> AuthProvider.EMAIL
            else -> AuthProvider.UNKNOWN
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user?.let {
                User(
                    uid = it.uid,
                    email = it.email,
                    displayName = it.displayName,
                    provider = AuthProvider.EMAIL
                )
            }
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

// Update this function in AuthRepository.kt

    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            // Send email verification
            result.user?.sendEmailVerification()?.await()

            // Update profile
            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
            )?.await()

            val user = result.user?.let {
                User(
                    uid = it.uid,
                    email = it.email,
                    displayName = displayName,
                    provider = AuthProvider.EMAIL
                )
            }
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Add this new function
    suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Add this function to check if email is verified
    fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user?.let {
                User(
                    uid = it.uid,
                    email = it.email,
                    displayName = it.displayName,
                    photoUrl = it.photoUrl?.toString(),
                    provider = AuthProvider.GOOGLE
                )
            }
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<User> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            val user = result.user?.let {
                User(
                    uid = it.uid,
                    phoneNumber = it.phoneNumber,
                    provider = AuthProvider.PHONE
                )
            }
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}