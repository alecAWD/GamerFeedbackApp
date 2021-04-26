package com.amd6563.cmpsc475project.roomdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDAO{
    @Insert
    fun registerUser(userEntity: UserEntity)
    //@Query("select * from crime where userEmail = (:email)")
    //fun getCrimesForUser(email: String?): LiveData<List<Crime>>
    @Query("select * from user where username = (:email)")
    fun getUser(email: String): LiveData<UserEntity?>
    @Query("select * from user where username = (:email)")
    fun getUserByEmail(email: String): LiveData<UserEntity?>
    @Insert
    fun addUser(user: UserEntity)
    @Insert
    fun addGame(game: Game)
    @Insert
    fun addFeedback(feedback: FeedBack)
    @Query("select * from feedback where gameTitle = (:gameTitle)")
    fun getFeedbackForGame(gameTitle: String?) : LiveData<List<FeedBack>>
    @Query("select * from feedback")
    fun getFeedback(): LiveData<List<FeedBack>>
    @Query("select * from Game where username = (:email)")
    fun getGamesForUser(email: String?) : LiveData<List<Game>>
    @Query("select * from game")
    fun getGames(): LiveData<List<Game>>


}