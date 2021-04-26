package com.amd6563.cmpsc475project

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

class GameViewFragment : Fragment() {
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
    private val gamesListViewModel : GamesListViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val inflater = MenuInflater(context)
        inflater.inflate(R.menu.game_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.new_game){
            val fragment = CreateFeedbackFragment()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }else if(item.itemId == R.id.games){
            userViewModel.resetGame()
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
        val view = inflater.inflate(R.layout.game_view, container, false)
        gameTitle = view.findViewById(R.id.editTextFeedbackTitle)
        gameReleaseDate = view.findViewById(R.id.editTextReleaseDate)
        gameDeveloper = view.findViewById(R.id.editTextDeveloper)
        gameImage = view.findViewById(R.id.imageView)

        gameTitle.setText(userViewModel.clickedGame!!.gameTitle)
        gameReleaseDate.setText(userViewModel.clickedGame!!.gameReleaseDate)
        gameDeveloper.setText(userViewModel.clickedGame!!.gameDeveloper)

        gameTitle.isEnabled = false
        gameReleaseDate.isEnabled = false
        gameDeveloper.isEnabled = false
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
}