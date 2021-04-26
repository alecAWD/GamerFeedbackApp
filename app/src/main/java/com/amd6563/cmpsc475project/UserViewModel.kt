package com.amd6563.cmpsc475project

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amd6563.cmpsc475project.roomdatabase.FeedBack
import com.amd6563.cmpsc475project.roomdatabase.Game
import com.amd6563.cmpsc475project.roomdatabase.UserEntity

data class LoginFormState(val usernameError: Int? = null,
                          val passwordError: Int? = null,
                          val isDataValid: Boolean = false)

data class RegFormState(val usernameError: Int? = null,
                        val passwordError: Int? = null,
                        val passwordsMismatchError: Int? = null,
                        val isDataValid: Boolean = false)


class UserViewModel: ViewModel() {
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _regForm = MutableLiveData<RegFormState>()
    val regFormState: LiveData<RegFormState> = _regForm

    var loggedInUser:UserEntity? = null

    var clickedGame: Game? = null

    var clickedFeedback : FeedBack? = null

    fun setGame(game: Game){
        clickedGame = game
    }

    fun resetGame(){
        clickedGame = null
    }
    fun setFeedback(feedback: FeedBack){
        clickedFeedback = feedback
    }


    fun login(user: UserEntity) {
        loggedInUser = user
    }

    fun loggedin(): Boolean {
        return loggedInUser != null
    }

    fun logout() {
        loggedInUser = null
        // reset form state
        resetLoginFormState()
        resetRegFormState()
    }
    fun resetRegFormState() {
        _regForm.value = RegFormState()
    }

    fun resetLoginFormState() {
        _loginForm.value = LoginFormState()
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun regDataChanged(username: String, pswd1: String, pswd2: String) {
        if (!isUserNameValid(username)) {
            _regForm.value = RegFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(pswd1)) {
            _regForm.value = RegFormState(passwordError = R.string.invalid_password)
        } else if (!pswdConfirmed(pswd1, pswd2)) {
            _regForm.value = RegFormState(passwordsMismatchError = R.string.passwords_do_not_match)
        } else {
            _regForm.value = RegFormState(isDataValid = true)
        }
    }

    fun isUserNameValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    // A placeholder password validation check
    fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun pswdConfirmed(pswd1: String, pswd2: String): Boolean {
        return pswd1 == pswd2
    }
}