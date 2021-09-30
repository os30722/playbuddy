package com.hfad.sports.di

import com.hfad.sports.api.EventApi
import com.hfad.sports.api.UserApi
import com.hfad.sports.api.AuthApi
import com.hfad.sports.api.MessageApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Retrofit {

    @Provides
    @Singleton
    fun providesUserApi(): AuthApi = AuthApi.create()

    @Provides
    @Singleton
    fun provideGameApi(): EventApi = EventApi.create()

    @Provides
    @Singleton
    fun provideSearchApi(): UserApi = UserApi.create()

    @Provides
    @Singleton
    fun provideMessageApi(): MessageApi = MessageApi.create()

}