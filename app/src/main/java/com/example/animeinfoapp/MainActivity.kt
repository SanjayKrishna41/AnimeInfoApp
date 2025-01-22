package com.example.animeinfoapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.animeinfoapp.databinding.ActivityMainBinding
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

        // observe top animme list
        animeViewModel.topAnime.observe(this, Observer { animeResponse ->
            when (animeResponse) {
                is Resource.Success -> {
                    Log.e("Main Activity", "Success")
                    animeResponse.data.let { result ->
                        result?.data.let { animeList ->
                            val size = animeList?.size
                            val title = animeList?.get(5)?.title

                            Log.e("Main Activity", "list size - $size\n sample title - $title")
                        }
                    }
                }

                is Resource.Error -> {
                    Log.e("Main Activity", "Error")
                    animeResponse.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading -> {
                    Log.e("Main Activity", "Loading ${animeResponse.data}")
                }
            }
        })

    }
}