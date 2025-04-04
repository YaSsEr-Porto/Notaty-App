package com.example.to_dolist

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.to_dolist.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileFragment : Fragment() {

    var _binding: FragmentProfileBinding? = null
    val binding get() = _binding!!
    lateinit var sharedPreferences: SharedPreferences
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
//    sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE)
//
//    binding.logoutBtn.setOnClickListener
//    {
//        logout()
//    }
//    return binding.root
//}
//
//fun logout() {
//    FirebaseAuth.getInstance().signOut()
//    sharedPreferences.edit().putBoolean("rememberMe", false).apply()
//
//    val intent = Intent(requireContext(), LoginActivity::class.java)
//    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//    startActivity(intent)
//    requireActivity().finish()
//}
//
//override fun onDestroyView() {
//    super.onDestroyView()
//    _binding = null
//}
//}