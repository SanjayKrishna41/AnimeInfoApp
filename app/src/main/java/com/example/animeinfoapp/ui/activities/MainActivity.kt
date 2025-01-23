package com.example.animeinfoapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.animeinfoapp.R
import com.example.animeinfoapp.databinding.ActivityMainBinding
import com.example.animeinfoapp.ui.fragments.TopAnimeFragment
import com.example.animeinfoapp.utils.Constants.Companion.NO_INTERNET
import com.example.animeinfoapp.utils.Constants.Companion.RETRY_BUTTON
import com.example.animeinfoapp.utils.Resource
import com.example.animeinfoapp.viewmodel.AnimeViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityBinding: ActivityMainBinding
    val animeViewModel: AnimeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(mainActivityBinding.fragmentLayout.id, TopAnimeFragment())
                .commit()
        }

        // network connection observer
        animeViewModel.networkStatus.observe(this, Observer { status ->
            if (status) {
                // if status is true - connected
                //do nothing
                Log.e("Main Activity","status $status")
            } else {
                // if status is false - not connected
                Snackbar.make(
                    mainActivityBinding.root,
                    NO_INTERNET,
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(RETRY_BUTTON, { animeViewModel.connectionRetry() })
                    setActionTextColor(getColor(R.color.black))
                }.show()
            }
        })

    }
}