package com.demo.chatapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.chatapp.R
import com.demo.chatapp.adapters.UsersAdapter
import com.demo.chatapp.data.model.User
import com.demo.chatapp.databinding.HomeFragmentBinding
import com.demo.chatapp.ui.ChatActivity
import com.demo.chatapp.utils.AppConstants
import com.demo.chatapp.utils.FirestoreUtil
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment.*

@AndroidEntryPoint
class HomeFragment : Fragment(), UsersAdapter.UserItemListener {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: HomeFragmentBinding
    private lateinit var userListenerRegistration: ListenerRegistration

    private lateinit var peopleSection: Section

    private lateinit var userAdapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(layoutInflater)


        userAdapter = UsersAdapter(this@HomeFragment)
        binding.recyclerViewPeople.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = userAdapter
        }

        userListenerRegistration =
            FirestoreUtil.addUsersListener(requireActivity(), this::updateRecyclerView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListenerRegistration)
    }

    private fun updateRecyclerView(items: List<DocumentSnapshot>) {
        userAdapter.setItems(items)
        userAdapter.notifyDataSetChanged()

    }

    override fun onClickedUser(id: String, user: User) {
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        intent.putExtra(AppConstants.USER_NAME, user.name)
        intent.putExtra(AppConstants.USER_ID, id)
        startActivity(intent)
    }

}