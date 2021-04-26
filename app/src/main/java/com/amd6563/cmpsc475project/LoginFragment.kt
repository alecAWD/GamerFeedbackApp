package com.amd6563.cmpsc475project

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.amd6563.cmpsc475project.roomdatabase.UserRepository

class LoginFragment : Fragment() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var  loginBTN : Button
    private lateinit var noAccountText: TextView

    private val userRepository = UserRepository.get()

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login, container, false)
        username = view.findViewById(R.id.editTextTextEmailAddress)
        password = view.findViewById(R.id.editTextTextPassword)
        loginBTN = view.findViewById(R.id.loginBTN)
        noAccountText = view.findViewById(R.id.noAccountTextView)

        loginBTN.isEnabled = false

        userViewModel.loginFormState.observe(viewLifecycleOwner, Observer {
            val loginState = it ?: return@Observer

            loginBTN.isEnabled = loginState.isDataValid

            if(loginState.usernameError != null){
                username.error = getString(loginState.usernameError)
            }
            if(loginState.passwordError != null){
                password.error = getString(loginState.passwordError)
            }
        })

        username.afterTextChanged{
            userViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }
        password.afterTextChanged {
            userViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }
        loginBTN.setOnClickListener{
            userRepository.getUserByEmail(username.text.toString()).observe(
                viewLifecycleOwner,
                Observer { user ->
                    if(user == null){
                        Toast.makeText(activity,"wrong login credentials", Toast.LENGTH_SHORT).show()
                    }else{
                        val hashedPassword = HashUtils.sha256(password.text.toString())
                        if(hashedPassword == user.password){
                            userViewModel.login(user)
                            val msg = "Welcome ${user.username}!"
                            val gameListFragment = GamesListFragment()
                            activity?.supportFragmentManager
                                ?.beginTransaction()
                                ?.replace(R.id.fragment_container, gameListFragment)
                                ?.addToBackStack(null)
                                ?.commit()
                        }
                    }
                }
            )
        }

        noAccountText.setOnClickListener {
            val fragment = RegisterFragment()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }
        return view
    }
    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }
}