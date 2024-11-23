package com.example.ecovolt.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecovolt.MainActivity
import com.example.ecovolt.R
import com.example.ecovolt.databinding.FragmentRegistersBinding
import com.example.ecovolt.model.Device
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegistersFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentRegistersBinding
    private lateinit var database: DatabaseReference
    private var devices = mutableListOf<Device>()
    private lateinit var userRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getAll()

        binding.addButton.setOnClickListener{
            navController.navigate(R.id.action_registersFragment_to_editFragment)
        }

        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    private fun getAll() {
        devices.clear()
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (medicineSnapshot in snapshot.children) {
                        val device = medicineSnapshot.getValue(Device::class.java)
                        if (device != null) {
                            devices.add(device)
                        }
                    }
                    devices.forEach {
                        println(it.name + it.consumption + it.type + it.percent + it.picPath)
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.recyclerView.adapter = MyAdapter(devices)
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

    inner class MyAdapter(private val itemList: List<Device>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.findViewById(R.id.name)
            val type: ImageView = itemView.findViewById(R.id.type)
            val consumption: TextView = itemView.findViewById(R.id.consumption)
            val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
            val percent: TextView = itemView.findViewById(R.id.percent)

            init {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val bundle = Bundle().apply {
                            putInt("position", position) //
                        }
                        navController.navigate(R.id.action_registersFragment_to_editFragment, bundle)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.registers_item, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item=itemList[position]
            holder.name.text=item.name
            holder.consumption.text=item.consumption.toString()
            holder.percent.text="${item.percent}%"
            val drawableResourceId=holder.itemView.context.resources.getIdentifier(
                item.picPath,"drawable",holder.itemView.context.packageName
            )
            if (drawableResourceId != 0) {
                Glide.with(holder.itemView.context).load(drawableResourceId).into(holder.type)
            } else {
                Glide.with(holder.itemView.context).clear(holder.type)
            }
            holder.progressBar.progress= item.percent!!
        }

        override fun getItemCount() = itemList.size
    }
}