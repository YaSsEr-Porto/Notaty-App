package com.example.to_dolist.ui.activities

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
import com.example.to_dolist.ui.fragments.FavouritesFragment
import com.example.to_dolist.viewmodel.NoteViewModel
import com.example.to_dolist.ui.fragments.NotesFragment
import com.example.to_dolist.ui.fragments.ProfileFragment
import com.example.to_dolist.R
import com.example.to_dolist.ui.fragments.SettingsFragment
import com.example.to_dolist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    lateinit var binding: ActivityMainBinding

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

        binding.botNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.home -> NotesFragment()
                R.id.fav -> FavouritesFragment()
                R.id.usr -> ProfileFragment()
                R.id.set -> SettingsFragment()
                else -> null
            }
            fragment?.let { loadFragment(it) }
            true
        }

        binding.addTaskBtn.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        if (savedInstanceState == null) loadFragment(NotesFragment()) else currentFragment()?.let { updateViewsForFragment(it) }
    }

    private fun currentFragment(): Fragment? = supportFragmentManager.findFragmentById(R.id.frag_container)

    fun filterNotes(query: String) {
        when (val fragment = currentFragment()) {
            is NotesFragment -> fragment.filterNotes(query)
            is FavouritesFragment -> fragment.filterNotes(query)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
                startActivity(Intent(this, TrashActivity::class.java))
                finish()
                true
            }

            R.id.about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun updateViewsForFragment(fragment: Fragment) {
        binding.toolbar.visibility = View.VISIBLE

        binding.searchBarContainer.visibility = when (fragment) {
            is NotesFragment, is FavouritesFragment -> View.VISIBLE
            else -> View.GONE
        }

        binding.addTaskBtn.visibility = when (fragment) {
            is NotesFragment -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun loadFragment(fragment: Fragment, tag: String? = null) {
        supportFragmentManager.beginTransaction().replace(R.id.frag_container, fragment, tag).commit()
        updateViewsForFragment(fragment)
    }

    fun applySavedTheme() {
        val themePref = getSharedPreferences("ThemePrefs", MODE_PRIVATE).getString("theme", "system")

        val mode = when (themePref) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}