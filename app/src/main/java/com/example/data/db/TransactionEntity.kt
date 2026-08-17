package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val coins: Int,
    val redeemCode: String?,
    val timestamp: String,
    val status: String
)

@Entity(tableName = "user_account")
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val coins: Int,
    val withdrawalCoins: Int = 0,
    val referralCode: String
)
