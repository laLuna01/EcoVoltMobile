package com.example.ecovolt.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecovolt.MainActivity
import com.example.ecovolt.R
import com.example.ecovolt.databinding.FragmentUserBinding
import com.example.ecovolt.model.Device
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentUserBinding
    private lateinit var database: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private var comsumption: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getComsumption()

        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

        binding.signOutButton.setOnClickListener {
            auth.signOut()
            navController.navigate(R.id.action_userFragment_to_splashFragment)
            (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        }
    }

    private fun getComsumption() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (deviceSnapshot in snapshot.children) {
                        val device = deviceSnapshot.getValue(Device::class.java)
                        if (device != null) {
                            comsumption += device.consumption
                        }
                    }
                    binding.totalComsumption.text = "${comsumption} KW"
                } else {
                    println("Nenhum dispositivo encontrado.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erro ao buscar os dispositivos: ${error.message}")
            }

        })
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ecovoltgs-default-rtdb.firebaseio.com/")
        val userEmail = auth.currentUser?.email?.replace(".", "_")
        if (userEmail != null) {
            userRef = database.child("users").child(userEmail).child("dispositivos")
        }
    }
}