package com.amd6563.cmpsc475project

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class FeedbackViewFragment : Fragment() {

    private lateinit var feedBackTitle: EditText
    private lateinit var gameFeedback: EditText
    private val userViewModel: UserViewModel by activityViewModels()

    private var isDeveloper : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val inflater = MenuInflater(context)
        inflater.inflate(R.menu.feedback_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.feedback){
            val fragment = FeedbackListFragment()
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
        val view = inflater.inflate(R.layout.feedback_view, container, false)
        feedBackTitle = view.findViewById(R.id.editTextFeedbackTitle)
        gameFeedback = view.findViewById(R.id.editTextFeedbackView)
        feedBackTitle.setText(userViewModel.clickedFeedback!!.feedbackTitle)
        gameFeedback.setText(userViewModel.clickedFeedback!!.feedback)
        feedBackTitle.isEnabled = false
        gameFeedback.isEnabled = false

        return view
    }
}