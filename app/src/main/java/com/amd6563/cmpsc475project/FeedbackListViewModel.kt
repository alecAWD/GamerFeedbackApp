package com.amd6563.cmpsc475project


import androidx.lifecycle.ViewModel
import com.amd6563.cmpsc475project.roomdatabase.UserRepository

class FeedbackListViewModel : ViewModel(){
    private val feedbackRepository = UserRepository.get()
    var feedbackListLiveData = feedbackRepository.getFeedback()

    fun setFeedBackListData(gameTitle: String){
        feedbackListLiveData = feedbackRepository.getFeedbackForGame(gameTitle)
    }
}