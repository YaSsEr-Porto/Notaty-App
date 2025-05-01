package com.example.to_dolist.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.to_dolist.R
import com.example.to_dolist.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.abt)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val version = packageManager.getPackageInfo(packageName, 0).versionName
        binding.tvVersion.text = "Version: $version"

        val contactOptions = mapOf(
            binding.devEmailContainer to Intent(Intent.ACTION_SENDTO).apply { data = "mailto:yasser.devtech@gmail.com".toUri() },
            binding.devGithubContainer to Intent(Intent.ACTION_VIEW).apply { data = "https://github.com/yassernabil2".toUri() },
            binding.devLinkedinContainer to Intent(Intent.ACTION_VIEW).apply { data = "https://www.linkedin.com/in/yassernabil-dev".toUri() },
            binding.devPhoneContainer to Intent(Intent.ACTION_VIEW).apply { data = "https://wa.me/201158485390?text=Hello%20Yasser!".toUri() })

        contactOptions.forEach { (btn, intent) ->
            btn.setOnClickListener { startActivity(intent) }
        }
    }
}