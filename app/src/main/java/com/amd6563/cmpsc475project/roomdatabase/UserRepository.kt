package com.amd6563.cmpsc475project.roomdatabase

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import java.util.concurrent.Executors
import com.amd6563.cmpsc475project.roomdatabase.UserEntity
import java.lang.IllegalStateException
import java.util.*

private const val DATABASE_NAME = "UserDatabase"

class UserRepository private constructor(context: Context){
    private val database : UserDatabase = Room.databaseBuilder(
        context.applicationContext,
        UserDatabase::class.java,
        DATABASE_NAME
    ).fallbackToDestructiveMigration().build()

    private val userDAO = database.userDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun addUser(user: UserEntity) {
        executor.execute{
            userDAO.addUser(user)
        }
    }
    fun addGame(game: Game){
        executor.execute {
            userDAO.addGame(game)
        }
    }
    fun addFeedback(feedback: FeedBack){
        executor.execute {
            userDAO.addFeedback(feedback)
        }
    }
    fun getFeedbackForGame(gameTitle: String?) : LiveData<List<FeedBack>> = userDAO.getFeedbackForGame(gameTitle)
    fun getGamesForUser(username: String?): LiveData<List<Game>> = userDAO.getGamesForUser(username)

    fun getGames(): LiveData<List<Game>> = userDAO.getGames()
    fun getFeedback(): LiveData<List<FeedBack>> = userDAO.getFeedback()

    fun getUserByEmail(email: String): LiveData<UserEntity?> = userDAO.getUserByEmail(email)

    companion object {
        private var INSTANCE: UserRepository? = null
        fun initialize(context: Context){
            if(INSTANCE == null){
                INSTANCE = UserRepository(context)
            }
        }
        fun get() : UserRepository{
            return INSTANCE?:
                    throw IllegalStateException("UserRepository must be initialized")
        }
    }
}