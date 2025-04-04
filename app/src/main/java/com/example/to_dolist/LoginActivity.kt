package com.example.to_dolist

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isEmpty
import com.example.to_dolist.databinding.ActivityLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var signUpLauncher: ActivityResultLauncher<Intent>
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scr_v)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)
        if (rememberMe && currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.chkBox.isChecked = rememberMe

        binding.btnLogin.setOnClickListener {

            val email = binding.inputEmail.text.toString().trim()
            val password = binding.inputPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (binding.chkBox.isChecked) {
                            sharedPreferences.edit().putBoolean("rememberMe", true).apply()
                        } else {
                            sharedPreferences.edit().putBoolean("rememberMe", false).apply()
                        }
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }

        binding.googleBtn.setOnClickListener {
            binding.chkBox.isChecked = true
            sharedPreferences.edit().putBoolean("rememberMe", true).apply()
            launchSignUpFlow(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
        }

        binding.emailBtn.setOnClickListener {
            launchSignUpFlow(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
        }

        signUpLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val response = IdpResponse.fromResultIntent(result.data)
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d(
                        "LoginActivity",
                        "Successful login: ${FirebaseAuth.getInstance().currentUser?.displayName}"
                    )
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.d("LoginActivity", "Unsuccessful login: ${response?.error?.errorCode}")
                }
            }
    }

    private fun launchSignUpFlow(providers: List<AuthUI.IdpConfig>) {
        signUpLauncher.launch(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build()
        )
    }
}