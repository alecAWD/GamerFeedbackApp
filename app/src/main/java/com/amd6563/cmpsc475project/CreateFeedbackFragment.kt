package com.amd6563.cmpsc475project

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.amd6563.cmpsc475project.roomdatabase.FeedBack
import com.amd6563.cmpsc475project.roomdatabase.UserRepository
import java.util.*

class CreateFeedbackFragment : Fragment() {
    private lateinit var gameTitle: TextView
    private lateinit var feedBackTitle: EditText
    private lateinit var gameFeedback: EditText
    private lateinit var gameImage: ImageView
    private lateinit var feedbackCreateBTN : Button
    private val userRepository = UserRepository.get()
    private lateinit var importantCheckBox : CheckBox
    private lateinit var newFeedback : FeedBack
    private val userViewModel: UserViewModel by activityViewModels()

    private var isDeveloper : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val inflater = MenuInflater(context)
        inflater.inflate(R.menu.logout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.games){
            val fragment = GamesListFragment()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }else if(item.itemId == R.id.logout){
            val fragment = LoginFragment()
            userViewModel.logout()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_feeback_fragment, container, false)
        importantCheckBox = view.findViewById(R.id.importantCHBX)
        gameTitle = view.findViewById(R.id.gameTitleTextView)
        feedBackTitle = view.findViewById(R.id.editTextFeedbackTitle)
        gameFeedback = view.findViewById(R.id.editTextFeedbackView)
        gameImage = view.findViewById(R.id.feedBackGameImage)
        feedbackCreateBTN = view.findViewById(R.id.createFeedbackBTN)
        gameTitle.setText(userViewModel.clickedGame!!.gameTitle)
        newFeedback = FeedBack(UUID.randomUUID(), "", "", "", "")
        val bitmap = BitmapFactory.decodeByteArray(
            userViewModel.clickedGame!!.gameImage,
            0,
            userViewModel.clickedGame!!.gameImage!!.size
        )

        gameImage.post(Runnable { gameImage.setImageBitmap(
            resizeBitmap(
                Bitmap.createScaledBitmap(
                    bitmap,
                    gameImage.width,
                    gameImage.height,
                    false
                ), 128
            )
        ) })

        feedbackCreateBTN.setOnClickListener{
            if(gameTitle.text.toString() != null && gameFeedback.text.toString() != null){
                val user = userViewModel.loggedInUser
                newFeedback.gameTitle = gameTitle.text.toString()
                newFeedback.feedback = gameFeedback.text.toString()
                newFeedback.feedbackTitle = feedBackTitle.text.toString()
                if (user != null) {
                    newFeedback.username = user.username
                    userRepository.addFeedback(newFeedback)
                    val fragment = GamesListFragment()
                    if(importantCheckBox.isChecked){
                        val recipient = userViewModel.clickedGame!!.username
                        val subject = newFeedback.feedbackTitle
                        val message = newFeedback.feedback
                        sendEmail(recipient, subject, message)
                    }
                    activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.fragment_container, fragment)
                        ?.addToBackStack(null)
                        ?.commit()
                }

            }
        }


        return view
    }
    override fun onStart(){
        super.onStart()
    }
    private fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
        try {
            if (source.height >= source.width) {
                if (source.height <= maxLength) { // if image height already smaller than the required height
                    return source
                }

                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                val result = Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
                return result
            } else {
                if (source.width <= maxLength) { // if image width already smaller than the required width
                    return source
                }

                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()

                val result = Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
                return result
            }
        } catch (e: Exception) {
            return source
        }
    }
    private fun sendEmail(recipient: String, subject: String, message: String){
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mIntent.putExtra(Intent.EXTRA_TEXT, message)
        try{
            startActivity(Intent.createChooser(mIntent,"Choose Email Client..."))
        }catch(e: Exception){
            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        }
    }
}