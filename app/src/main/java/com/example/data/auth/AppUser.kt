package com.example.data.auth

data class AppUser(
  val uid: String,
  val email: String?,
  val displayName: String?,
  val phoneNumber: String? = null,
  val isGuest: Boolean = false,
  val photoUrl: String? = null
)
