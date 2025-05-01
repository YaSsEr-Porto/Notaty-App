package com.example.to_dolist.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.to_dolist.viewmodel.NoteViewModel
import com.example.to_dolist.R
import com.example.to_dolist.databinding.ActivityLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
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

        val viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
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

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val newUserId = FirebaseAuth.getInstance().currentUser?.uid
                    sharedPreferences.edit().putBoolean("rememberMe", binding.chkBox.isChecked).apply()

                    val oldUserId = getLastLoggedInUserId()

                    if (newUserId != null && newUserId != oldUserId) {
                        saveLoggedInUserId(newUserId)
                        viewModel.switchUserAndRestoreNotes(newUserId)
                    }
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.forgotPass.apply {
            setTextColor(getColor(R.color.blue_700))
            setOnClickListener { showResetPasswordDialog() }
        }

        binding.googleBtn.setOnClickListener {
            sharedPreferences.edit().putBoolean("rememberMe", true).apply()
            launchSignUpFlow(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
        }

        binding.emailBtn.setOnClickListener {
            sharedPreferences.edit().putBoolean("rememberMe", true).apply()
            launchSignUpFlow(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
        }

        binding.guestBtn.setOnClickListener {
            with(sharedPreferences.edit()) {
                putBoolean("rememberMe", true)
                remove("loggedInUserId")
                apply()
            }
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        signUpLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val response = IdpResponse.fromResultIntent(result.data)
            if (result.resultCode == RESULT_OK) {
                val newUserId = FirebaseAuth.getInstance().currentUser?.uid
                val oldUserId = getLastLoggedInUserId()

                if (newUserId != null && newUserId != oldUserId) {
                    saveLoggedInUserId(newUserId)
                    viewModel.switchUserAndRestoreNotes(newUserId)
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun showResetPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reset_password, null)
        val emailInput = dialogView.findViewById<TextInputEditText>(R.id.dialog_reset_password_input_email)

        val dialog = MaterialAlertDialogBuilder(this).apply {
            setIcon(R.drawable.ic_email)
            setTitle("Reset Password")
            setMessage("Enter your email to receive reset link")
            setView(dialogView)
            setPositiveButton("Send", null)
            setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        }.create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty()) {
                emailInput.error = "Please enter your email"
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener {
                    Toast.makeText(this@LoginActivity, "Reset link sent to your email", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }.addOnFailureListener {
                    Toast.makeText(this@LoginActivity, "Failed to send reset link", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun launchSignUpFlow(providers: List<AuthUI.IdpConfig>) {
        signUpLauncher.launch(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
        )
    }

    private fun saveLoggedInUserId(uid: String) {
        sharedPreferences.edit().putString("loggedInUserId", uid).apply()
    }

    private fun getLastLoggedInUserId(): String? {
        return sharedPreferences.getString("loggedInUserId", null)
    }
}