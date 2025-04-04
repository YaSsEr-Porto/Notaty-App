package com.example.to_dolist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.to_dolist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    lateinit var binding: ActivityMainBinding
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {

        applySavedTheme()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cons)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterNotes(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterNotes(it) }
                return true
            }

        })

        if (savedInstanceState == null) loadFragment(NotesFragment())

        binding.addTaskBtn.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        binding.botNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(NotesFragment())
                R.id.fav -> loadFragment(FavouritesFragment())
                R.id.usr -> loadFragment(ProfileFragment())
                R.id.set -> loadFragment(SettingsFragment())
            }
            true
        }
    }

    fun filterNotes(query: String) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frag_container)
        when (currentFragment) {
            is NotesFragment -> currentFragment.filterNotes(query)
            is FavouritesFragment -> currentFragment.filterNotes(query)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frag_container)
        return when (item.itemId) {
            R.id.sort_by_title -> {
                noteViewModel.setSorting("title")
                true
            }

            R.id.sort_by_date -> {
                noteViewModel.setSorting("date")
                true
            }

            R.id.trash -> {
                val i = Intent(this, TrashActivity::class.java)
                startActivity(i)
                true
            }

            R.id.about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun updateToolbarForFragment(fragment: Fragment) {
        when (fragment) {
            is NotesFragment -> {
                binding.toolbar.visibility = View.VISIBLE
                binding.searchBarContainer.visibility = View.VISIBLE
                binding.addTaskBtn.visibility = View.VISIBLE
            }

            is FavouritesFragment -> {
                binding.toolbar.visibility = View.VISIBLE
                binding.searchBarContainer.visibility = View.VISIBLE
                binding.addTaskBtn.visibility = View.GONE
            }

            else -> {
                binding.toolbar.visibility = View.GONE
                binding.searchBarContainer.visibility = View.GONE
                binding.addTaskBtn.visibility = View.GONE
            }
        }
    }

    fun loadFragment(fragment: Fragment, tag: String? = null) {
        supportFragmentManager.beginTransaction().replace(R.id.frag_container, fragment, tag).commit()
        updateToolbarForFragment(fragment)
    }

    fun applySavedTheme() {
        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        when (sharedPreferences.getString("theme", "system")) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}