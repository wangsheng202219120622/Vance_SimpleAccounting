package com.deweiwang.bookkeeping.di

import android.content.Context
import androidx.room.Room
import com.deweiwang.bookkeeping.data.AccountDao
import com.deweiwang.bookkeeping.data.AccountDatabase
import com.deweiwang.bookkeeping.data.UserPreferences
import com.deweiwang.bookkeeping.data.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AccountDatabase {
        return Room.databaseBuilder(
            context,
            AccountDatabase::class.java,
            "account_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideAccountDao(database: AccountDatabase): AccountDao {
        return database.accountDao()
    }

    @Singleton
    @Provides
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Singleton
    @Provides
    fun provideUserPreferencesRepository(userPreferences: UserPreferences): UserPreferencesRepository {
        return UserPreferencesRepository(userPreferences)
    }
}