package com.ferrytech.n_droid.data.model

data class User(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val provider: AuthProvider = AuthProvider.UNKNOWN
)

enum class AuthProvider {
    EMAIL,
    GOOGLE,
    PHONE,
    UNKNOWN
}