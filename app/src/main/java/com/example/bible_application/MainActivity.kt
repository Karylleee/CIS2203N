package com.example.bible_application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.bible_application.data.importer.BibleJsonImporter
import com.example.bible_application.data.local.AppDatabase
import com.example.bible_application.data.repository.BibleRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var isProgrammaticSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        // Custom listener to avoid clearing the backstack on programmatic selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            if (isProgrammaticSelection) {
                true
            } else {
                androidx.navigation.ui.NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
        bottomNavigationView.setOnItemReselectedListener { item ->
            if (!isProgrammaticSelection) {
                navController.popBackStack(item.itemId, false)
            }
        }

        // Dynamically synchronize bottom menu tab highlight based on sub-destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_books,
                R.id.navigation_chapters,
                R.id.navigation_verses -> {
                    setCheckedItemSafely(bottomNavigationView, R.id.navigation_books)
                }
                R.id.navigation_home -> {
                    setCheckedItemSafely(bottomNavigationView, R.id.navigation_home)
                }
                R.id.navigation_search -> {
                    setCheckedItemSafely(bottomNavigationView, R.id.navigation_search)
                }
                R.id.navigation_saved -> {
                    setCheckedItemSafely(bottomNavigationView, R.id.navigation_saved)
                }
            }
        }

        // Trigger Bible JSON import in background coroutine on first run
        val database = AppDatabase.getDatabase(this)
        val repository = BibleRepository(
            database.bibleVerseDao(),
            database.bookmarkDao(),
            database.highlightDao()
        )
        val importer = BibleJsonImporter(this, repository)

        lifecycleScope.launch {
            importer.importIfNeeded()
        }
    }

    private fun setCheckedItemSafely(bottomNavigationView: BottomNavigationView, itemId: Int) {
        val menu = bottomNavigationView.menu
        val item = menu.findItem(itemId) ?: return
        if (item.isChecked) return
        
        isProgrammaticSelection = true
        bottomNavigationView.setOnItemSelectedListener(null)
        bottomNavigationView.setOnItemReselectedListener(null)
        
        item.isChecked = true
        
        bottomNavigationView.setOnItemSelectedListener { selectedItem ->
            androidx.navigation.ui.NavigationUI.onNavDestinationSelected(selectedItem, navController)
        }
        bottomNavigationView.setOnItemReselectedListener { selectedItem ->
            navController.popBackStack(selectedItem.itemId, false)
        }
        isProgrammaticSelection = false
    }
}