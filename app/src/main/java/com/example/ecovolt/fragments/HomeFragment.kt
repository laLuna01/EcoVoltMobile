package com.example.ecovolt.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.ecovolt.MainActivity
import com.example.ecovolt.R
import com.example.ecovolt.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        Glide.with(requireContext())
            .load(R.drawable.item_1)
            .into(binding.itemImage)
        Glide.with(requireContext())
            .load(R.drawable.item_2)
            .into(binding.itemImage1)
        Glide.with(requireContext())
            .load(R.drawable.item_3)
            .into(binding.itemImage2)
        Glide.with(requireContext())
            .load(R.drawable.item_4)
            .into(binding.itemImage3)
        Glide.with(requireContext())
            .load(R.drawable.item_5)
            .into(binding.itemImage4)
        Glide.with(requireContext())
            .load(R.drawable.item_6)
            .into(binding.itemImage5)
        Glide.with(requireContext())
            .load(R.drawable.item_7)
            .into(binding.itemImage6)
        Glide.with(requireContext())
            .load(R.drawable.item_8)
            .into(binding.itemImage7)
        Glide.with(requireContext())
            .load(R.drawable.item_9)
            .into(binding.itemImage8)

        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

}