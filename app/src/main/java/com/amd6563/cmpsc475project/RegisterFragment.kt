package com.amd6563.cmpsc475project

import android.app.Activity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.amd6563.cmpsc475project.roomdatabase.UserEntity
import com.amd6563.cmpsc475project.roomdatabase.UserRepository
import java.util.*


class RegisterFragment : Fragment() {
    private lateinit var username : EditText
    private lateinit var password : EditText
    private lateinit var confirmPassword : EditText
    private lateinit var playerCHBX : RadioButton
    private lateinit var developerCHBX : RadioButton
    private lateinit var radioGroup: RadioGroup
    private lateinit var registerBTN : Button
    private lateinit var haveAccountView: TextView
    private var newUser : UserEntity = UserEntity(UUID.randomUUID(), "", "", false)
    private val userRepository = UserRepository.get()
    private val userViewModel: UserViewModel by activityViewModels()


    private var isDeveloper : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.register, container, false)
        username = view.findViewById(R.id.editTextEmailAddress)
        password = view.findViewById(R.id.editTextPassword)
        confirmPassword = view.findViewById(R.id.editTextConfirmPassword)
        playerCHBX = view.findViewById(R.id.playerCHBX)
        developerCHBX = view.findViewById(R.id.developerCHBX)
        radioGroup = view.findViewById(R.id.radioGroup)
        registerBTN = view.findViewById(R.id.registerBTN)
        haveAccountView = view.findViewById(R.id.haveAccountTextView)

        registerBTN.isEnabled = false

        haveAccountView.setOnClickListener {
            val fragment = LoginFragment()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        username.afterTextChanged{
            userViewModel.regDataChanged(
                username.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString()
            )
        }
        password.afterTextChanged {
            userViewModel.regDataChanged(
                username.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString()
            )
        }
        confirmPassword.afterTextChanged {
            userViewModel.regDataChanged(
                username.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString()
            )
        }

        userViewModel.regFormState.observe(viewLifecycleOwner, Observer {
            val regState = it ?: return@Observer

            // disable login button unless both username / password is valid
            registerBTN.isEnabled = regState.isDataValid

            if (regState.usernameError != null) {
                username.error = getString(regState.usernameError)
            }
            if (regState.passwordError != null) {
                password.error = getString(regState.passwordError)
            }
            if (regState.passwordsMismatchError != null) {
                confirmPassword.error = getString(regState.passwordsMismatchError)
            }
        })

        registerBTN.setOnClickListener {
            if(password.text.toString() == confirmPassword.text.toString()){
                newUser.username = username.text.toString()
                newUser.password = HashUtils.sha256(password.text.toString())
                if(playerCHBX.isChecked){
                    newUser.type = false
                }else if(developerCHBX.isChecked){
                    newUser.type = true
                }
                userRepository.getUserByEmail(newUser.username).observe(
                    viewLifecycleOwner,
                    Observer { newUser ->
                        if (newUser == null) {
                            userRepository.addUser(this.newUser)
                            val msg = "The user ${this.newUser.username} is created"
                            val bundle = bundleOf("msg" to msg)
                            Toast.makeText(activity, "User registered", Toast.LENGTH_SHORT).show()

                            val fragment = LoginFragment()
                            activity?.supportFragmentManager
                                ?.beginTransaction()
                                ?.replace(R.id.fragment_container, fragment)
                                ?.addToBackStack(null)
                                ?.commit()
                        } else {
                            //Toast.makeText(
                            //    activity,
                            //    "the user ${newUser.username} already exists",
                            //    Toast.LENGTH_SHORT
                            //).show()

                        }
                    }
                )
            }
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
    override fun onStart(){
        super.onStart()
    }


}