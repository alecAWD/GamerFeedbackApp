package com.amd6563.cmpsc475project.roomdatabase

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UserEntity::class, Game::class, FeedBack::class], version=9, exportSchema = false)
@TypeConverters(UserTypeConverters::class)
abstract class UserDatabase : RoomDatabase(){
    abstract fun userDao(): UserDAO
}