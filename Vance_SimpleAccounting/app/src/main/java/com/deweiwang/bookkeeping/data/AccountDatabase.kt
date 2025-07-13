package com.deweiwang.bookkeeping.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Expense::class, Income::class], version = 1, exportSchema = false)
abstract class AccountDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}