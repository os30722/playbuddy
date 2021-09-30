package com.hfad.sports.di

import android.content.Context
import com.hfad.sports.db.EventDatabase
import com.hfad.sports.db.MessageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Database {

    @Provides
    @Singleton
    fun provideEventDatabase(@ApplicationContext context: Context) = EventDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideMessageDatabase(@ApplicationContext context: Context) = MessageDatabase.getInstance(context)

}