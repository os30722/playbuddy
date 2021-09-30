package com.hfad.sports.di

import com.hfad.sports.api.EventApi
import com.hfad.sports.api.UserApi
import com.hfad.sports.api.AuthApi
import com.hfad.sports.api.MessageApi
import com.hfad.sports.db.EventDatabase
import com.hfad.sports.db.MessageDatabase
import com.hfad.sports.repository.EventRepository
import com.hfad.sports.repository.MessageRepository
import com.hfad.sports.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Repository{

    @Provides
    @Singleton
    fun provideUserRepo(authApi: AuthApi, userApi: UserApi): UserRepository {
        return UserRepository(authApi, userApi)
    }

    @Provides
    @Singleton
    fun provideGameRepo(api: EventApi, database: EventDatabase): EventRepository {
        return EventRepository(api, database)
    }

    @Provides
    @Singleton
    fun provideMsgRepo(api: MessageApi, database: MessageDatabase): MessageRepository {
        return MessageRepository(api, database)
    }


}