package com.deweiwang.bookkeeping.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_table")
data class Income(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val amount: Double,
    val date: String,
    val time: String,
    val note: String
)