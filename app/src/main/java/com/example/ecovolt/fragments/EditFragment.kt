package com.example.ecovolt.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ecovolt.MainActivity
import com.example.ecovolt.R
import com.example.ecovolt.databinding.FragmentEditBinding
import com.example.ecovolt.model.Device
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentEditBinding
    private lateinit var database: DatabaseReference
    private var devices = mutableListOf<Device>()
    private lateinit var userRef: DatabaseReference
    private var position: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            position = it.getInt("position")
        }

        init(view)

        getAll()

        binding.cancelButton.setOnClickListener{
            navController.navigate(R.id.action_editFragment_to_registersFragment)
        }

        binding.saveButton.setOnClickListener{
            val name = binding.name.text.toString().trim()
            val consumption = binding.consumption.text.toString().toInt()
            val type = binding.spinner.selectedItem.toString().trim()

            if (position != -1) {
                val updatedDevice = Device(name, consumption, type)
                val picpathupdate = updatedDevice.picPath ?: "dashboard_36"
                val percentupdate = updatedDevice.percent ?: 0
                val updatedData = mapOf<String, Any>(
                    "name" to updatedDevice.name,
                    "consumption" to updatedDevice.consumption,
                    "picPath" to picpathupdate,
                    "percent" to percentupdate,
                    "type" to updatedDevice.type
                )
                userRef.child(position.toString()).updateChildren(updatedData)
            } else {
                devices.add(Device(name, consumption, type))
                userRef.setValue(devices)
            }

            navController.navigate(R.id.action_editFragment_to_registersFragment)
        }

        binding.deleteButton.setOnClickListener{
            userRef.child(position.toString()).removeValue()

            navController.navigate(R.id.action_editFragment_to_registersFragment)
        }

        val options = listOf("Eletrodoméstico Cozinha", "Eletrodoméstico Lavanderia", "Climatização", "Entretenimento e Eletrônicos", "Iluminação", "Eletroportáteis", "Outros")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    private fun getAll() {
        devices.clear()
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (deviceSnapshot in snapshot.children) {
                        val device = deviceSnapshot.getValue(Device::class.java)
                        if (device != null) {
                            devices.add(device)
                        }
                    }
                    devices.forEach {
                        println(it.name + it.consumption + it.type)
                    }
                    if (position != -1) {
                        binding.editDeviceTitle.text = "Editar dispositivo"
                        binding.deleteButton.visibility = View.VISIBLE
                        binding.name.setText(devices[position].name)
                        binding.consumption.setText(devices[position].consumption.toString())
                        val selection = when (devices[position].type.trim().lowercase()) {
                            "eletrodoméstico cozinha" -> 0
                            "eletrodoméstico lavanderia" -> 1
                            "climatização" -> 2
                            "entretenimento e eletrônicos" -> 3
                            "iluminação" -> 4
                            "eletroportáteis" -> 5
                            else -> 6
                        }
                        binding.spinner.setSelection(selection)
                    } else {
                        binding.deleteButton.visibility = View.GONE
                    }
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