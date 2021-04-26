package com.amd6563.cmpsc475project

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amd6563.cmpsc475project.roomdatabase.Game

class GamesListFragment : Fragment(){
    private var gamesRecyclerView : RecyclerView? = null
    private var adapter: GameAdapter? = GameAdapter(emptyList())
    val userViewModel: UserViewModel by activityViewModels()
    val feedbackViewModel: FeedbackListViewModel by activityViewModels()

    private val gamesListViewModel : GamesListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.game_list_fragment, container, false)
        gamesRecyclerView = view.findViewById(R.id.game_recycler_view) as RecyclerView
        gamesRecyclerView!!.layoutManager = LinearLayoutManager(context)
        gamesRecyclerView!!.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gamesListViewModel.gamesListLiveData.observe(
            viewLifecycleOwner,
            Observer { games ->
                games?.let {
                    updateUI(games)
                }
            }
        )
    }

    private fun updateUI(games: List<Game>){
        adapter = GameAdapter(games)
        gamesRecyclerView!!.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val inflater = MenuInflater(context)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.new_game && userViewModel.loggedInUser!!.type == true){
            val fragment = CreateGameFragment()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }else if(item.itemId == R.id.new_game && userViewModel.loggedInUser!!.type == false) {
            Toast.makeText(
                context,
                "You are not a developer, you can not add a game",
                Toast.LENGTH_SHORT
            ).show()
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






    private inner class GameHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var game: Game

        private val gameTitle: TextView = itemView.findViewById(R.id.game_title)
        private val gameReleaseDate: TextView = itemView.findViewById(R.id.game_date)
        private val gameImage : ImageView = itemView.findViewById(R.id.game_image)


        init{
            itemView.setOnClickListener(this)
        }

        fun bind(game: Game){
            this.game = game
            gameTitle.text = "Game: " + this.game.gameTitle.toString()
            gameReleaseDate.text = "Release Date: " + this.game.gameReleaseDate.toString()

            val bitmap = BitmapFactory.decodeByteArray(
                this.game.gameImage,
                0,
                this.game.gameImage!!.size
            )

            gameImage.post(Runnable { gameImage.setImageBitmap(
                resizeBitmap(
                    Bitmap.createScaledBitmap(
                        bitmap,
                        gameImage.width,
                        gameImage.height,
                        false
                    ), 64
                )
            ) })




        }

        override fun onClick(p0: View?) {
            Toast.makeText(context, "${game.gameTitle} clicked!", Toast.LENGTH_SHORT).show()
            if(userViewModel!!.loggedInUser!!.type == true && game.username == userViewModel!!.loggedInUser!!.username){
                userViewModel.setGame(game)
                feedbackViewModel.setFeedBackListData(game.gameTitle)
                val fragment = FeedbackListFragment()
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }else if(userViewModel!!.loggedInUser!!.type == false){
                feedbackViewModel.setFeedBackListData(game.gameTitle)
                userViewModel.setGame(game)
                val fragment = GameViewFragment()
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragment)
                    ?.addToBackStack(null)
                    ?.commit()

            }
            if(userViewModel.loggedInUser!!.type == true && game.username != userViewModel.loggedInUser!!.username){
                feedbackViewModel.setFeedBackListData(game.gameTitle)
                userViewModel.setGame(game)
                val fragment = GameViewFragment()
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }
    }



    private inner class GameAdapter(var games: List<Game>) : RecyclerView.Adapter<GameHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {
            val view = layoutInflater.inflate(R.layout.list_item_game, parent, false)
            return GameHolder(view)
        }
        override fun onBindViewHolder(holder: GameHolder, position: Int){
            val game = games[position]
            holder.bind(game)
        }

        override fun getItemCount() = games.size
    }

    companion object{
        fun newInstance():GamesListFragment{
            return GamesListFragment()
        }
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