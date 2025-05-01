package com.example.to_dolist.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.to_dolist.viewmodel.NoteViewModel
import com.example.to_dolist.R
import com.example.to_dolist.databinding.FragmentProfileBinding
import com.example.to_dolist.ui.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    lateinit var sharedPreferences: SharedPreferences

    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE)

        if (user == null) showGuestLayout() else setupProfile()

        return binding.root
    }

    fun showGuestLayout() {
        binding.profileContentLayout.visibility = View.GONE
        binding.profileImage.visibility = View.GONE
        binding.nest.isNestedScrollingEnabled = false
        binding.guestLayout.visibility = View.VISIBLE
        Glide.with(this).load(R.drawable.blur).into(binding.profileBackground)

        binding.guestLoginBtn.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

    fun setupProfile() {
        binding.profileContentLayout.visibility = View.VISIBLE
        binding.profileImage.visibility = View.VISIBLE
        binding.nest.isNestedScrollingEnabled = true
        binding.guestLayout.visibility = View.GONE

        loadUserData()

        val pickPI = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { binding.profileImage.setImageURI(it) }
        }

        val pickBI = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { binding.profileBackground.setImageURI(it) }
        }

        binding.pfFab.setOnClickListener { pickPI.launch("image/*") }

        binding.bgFab.setOnClickListener { pickBI.launch("image/*") }

        binding.btnLogout.setOnClickListener { logout() }

        var isEditing = false

        binding.editNameBtn.setOnClickListener {

            if (!isEditing) {
                binding.tvUserName.visibility = View.GONE
                binding.etUserName.setText(binding.tvUserName.text)
                binding.etUserName.visibility = View.VISIBLE
                binding.etUserName.requestFocus()
                binding.editNameBtn.setImageResource(R.drawable.done)
                isEditing = true

            } else {
                binding.editNameBtn.setImageResource(R.drawable.ic_edit)

                val newName = binding.etUserName.text.toString().trim()
                if (newName.isNotEmpty()) {
                    binding.tvUserName.text = newName
                    user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(newName).build())?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(requireContext(), "Name updated successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }

                binding.tvUserName.visibility = View.VISIBLE
                binding.etUserName.visibility = View.GONE
                binding.editNameBtn.setImageResource(R.drawable.ic_edit)
                isEditing = false
            }
        }

        binding.syncCard.setOnClickListener {
            val viewModel = ViewModelProvider(requireActivity()).get(NoteViewModel::class.java)
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                viewModel.syncNotesNow(userId)
                viewModel.switchUserAndRestoreNotes(userId)
                Toast.makeText(requireContext(), "Sync Started!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please login to sync notes.", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    fun loadUserData() {
        user?.let {
            binding.tvUserName.text = it.displayName
            binding.cardSignedEmail.text = it.email ?: "No Email"
            binding.cardUserId.text = it.uid

            val creationTime = it.metadata?.creationTimestamp?.let { time ->
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(time))

            } ?: "N/A"
            binding.cardAccountTime.text = creationTime

            Glide.with(this).load(it.photoUrl).placeholder(R.drawable.account_circle).error(R.drawable.account_circle).into(binding.profileImage)
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        sharedPreferences.edit { putBoolean("rememberMe", false) }

        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }
}