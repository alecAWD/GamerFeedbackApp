package com.amd6563.cmpsc475project

import androidx.lifecycle.ViewModel
import com.amd6563.cmpsc475project.roomdatabase.UserRepository

class GamesListViewModel : ViewModel(){
    private val gameRepository = UserRepository.get()
    var gamesListLiveData = gameRepository.getGames()

    fun setGames(){
        gamesListLiveData = gameRepository.getGames()
    }
}