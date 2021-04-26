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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amd6563.cmpsc475project.roomdatabase.FeedBack
import com.amd6563.cmpsc475project.roomdatabase.Game

class FeedbackListFragment : Fragment(){
    private lateinit var feedbackRecyclerView : RecyclerView
    private var adapter: FeedbackAdapter? = FeedbackAdapter(emptyList())
    val userViewModel: UserViewModel by activityViewModels()

    private val feedbackListViewModel : FeedbackListViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.feedback_list_fragment, container, false)
        feedbackListViewModel.setFeedBackListData(userViewModel.clickedGame!!.gameTitle)
        feedbackRecyclerView = view.findViewById(R.id.feedback_recycler_view) as RecyclerView
        feedbackRecyclerView.layoutManager = LinearLayoutManager(context)
        feedbackRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedbackListViewModel.feedbackListLiveData.observe(
            viewLifecycleOwner,
            Observer { feedback ->
                feedback?.let {
                    updateUI(feedback)
                }
            }
        )
    }

    private fun updateUI(feedback: List<FeedBack>){
        adapter = FeedbackAdapter(feedback)
        feedbackRecyclerView.adapter = adapter
    }

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






    private inner class FeedbackHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var feedback: FeedBack

        private val gameTitle: TextView = itemView.findViewById(R.id.feedback_title)
        private val feedBackView: TextView = itemView.findViewById(R.id.feedback)

        init{
            itemView.setOnClickListener(this)
        }

        fun bind(feedback: FeedBack){
            this.feedback = feedback
            gameTitle.text = "Title: " + this.feedback.feedbackTitle.toString()
            feedBackView.text = "Feedback: " + this.feedback.feedback.toString()

        }

        override fun onClick(p0: View?) {
            Toast.makeText(context, "${feedback.gameTitle} clicked!", Toast.LENGTH_SHORT).show()
            userViewModel.setFeedback(feedback)
            val fragment = FeedbackViewFragment()
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }
    }



    private inner class FeedbackAdapter(var feedbacks: List<FeedBack>) : RecyclerView.Adapter<FeedbackHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackHolder {
            val view = layoutInflater.inflate(R.layout.list_item_feedback, parent, false)
            return FeedbackHolder(view)
        }
        override fun onBindViewHolder(holder: FeedbackHolder, position: Int){
            val feedback = feedbacks[position]
            holder.bind(feedback)
        }

        override fun getItemCount() = feedbacks.size
    }

    companion object{
        fun newInstance():FeedbackListFragment{
            return FeedbackListFragment()
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