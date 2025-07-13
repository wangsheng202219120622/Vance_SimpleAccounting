package com.deweiwang.bookkeeping.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val amount: Double,
    val date: String,
    val time: String,
    val note: String
)