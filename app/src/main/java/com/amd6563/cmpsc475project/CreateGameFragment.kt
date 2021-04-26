package com.amd6563.cmpsc475project

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.amd6563.cmpsc475project.roomdatabase.Game
import com.amd6563.cmpsc475project.roomdatabase.UserRepository
import java.io.ByteArrayOutputStream
import java.util.*

class CreateGameFragment : Fragment() {
    private lateinit var gameTitle: EditText
    private lateinit var gameReleaseDate: EditText
    private lateinit var gameDeveloper : EditText
    private lateinit var gameCreateBTN : Button
    private lateinit var uploadImageBTN : Button
    private lateinit var gameImage : ImageView
    private val userRepository = UserRepository.get()
    private lateinit var newGame : Game
    private val pickImage = 100
    private var imageURI: Uri? = null
    private val userViewModel: UserViewModel by activityViewModels()

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
        val view = inflater.inflate(R.layout.create_game_fragment, container, false)
        gameTitle = view.findViewById(R.id.editTextFeedbackTitle)
        gameReleaseDate = view.findViewById(R.id.editTextReleaseDate)
        gameDeveloper = view.findViewById(R.id.editTextDeveloper)
        gameCreateBTN = view.findViewById(R.id.createGameBTN)
        gameImage = view.findViewById(R.id.imageView)
        uploadImageBTN = view.findViewById(R.id.uploadBTN)

        uploadImageBTN.setOnClickListener{
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        gameCreateBTN.setOnClickListener{
            if(gameTitle.text.toString() != null && gameReleaseDate.text.toString() != null && gameDeveloper.text.toString() != null){
                newGame = Game(UUID.randomUUID(), "", "", "", null, userViewModel.loggedInUser!!.username)
                val user = userViewModel.loggedInUser
                newGame.gameDeveloper = gameDeveloper.text.toString()
                newGame.gameReleaseDate = gameReleaseDate.text.toString()
                newGame.gameTitle = gameTitle.text.toString()
                val bitmap = gameImage.drawable.toBitmap()
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                newGame.gameImage = stream.toByteArray()
                if (user != null) {
                    newGame.username = user.username
                    userRepository.addGame(newGame)
                }
                val fragment = GamesListFragment()
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }



        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK && requestCode == pickImage){
            imageURI = data?.data
            gameImage.setImageURI(imageURI)
            gameImage.setImageBitmap(resizeBitmap(gameImage.drawable.toBitmap(), 128))
        }
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


}