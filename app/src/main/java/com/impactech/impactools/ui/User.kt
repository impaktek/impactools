package com.impactech.impactools.ui


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("data")
    val `data`: Data,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Int
) {
    @Serializable
    data class Data(
        @SerialName("about")
        val about: String?,
        @SerialName("account_notification")
        val accountNotification: Boolean,
        @SerialName("address")
        val address: String?,
        @SerialName("device_id")
        val deviceId: String?,
        @SerialName("email")
        val email: String,
        @SerialName("first_name")
        val firstName: String,
        @SerialName("gender")
        val gender: String?,
        @SerialName("id")
        val id: Int,
        @SerialName("image")
        val image: String?,
        @SerialName("is_email_verified")
        val isEmailVerified: Boolean,
        @SerialName("is_phone_verified")
        val isPhoneVerified: Boolean,
        @SerialName("last_login")
        val lastLogin: String?,
        @SerialName("last_name")
        val lastName: String,
        @SerialName("login_count")
        val loginCount: String?,
        @SerialName("news_notification")
        val newsNotification: Boolean,
        @SerialName("nin")
        val nin: String?,
        @SerialName("occupation")
        val occupation: String?,
        @SerialName("phone_number")
        val phoneNumber: String,
        @SerialName("referral_code")
        val referralCode: String,
        @SerialName("service_notification")
        val serviceNotification: Boolean,
        @SerialName("ssn")
        val ssn: String?,
        @SerialName("status")
        val status: String,
        @SerialName("token")
        val token: String,
        @SerialName("user_id")
        val userId: String,
        @SerialName("user_type")
        val userType: String,
        @SerialName("wallet_pin")
        val walletPin: String?,
        @SerialName("wallet_status")
        val walletStatus: Boolean
    )
}