package com.example.animeinfoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.animeinfoapp.databinding.ActivityMainBinding
import com.example.animeinfoapp.utils.CheckNetworkConnection
import com.example.animeinfoapp.utils.Constants.Companion.NO_INTERNET
import com.example.animeinfoapp.utils.Constants.Companion.RETRY_BUTTON
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var mainActivityBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        isNetworkAvailable()

    }

    // function to check network connection
    private fun isNetworkAvailable() {
        val checkNetworkConnection = CheckNetworkConnection(this)

        checkNetworkConnection.observe(this, Observer { status ->
            if (status) {
                // if status is true - connected
                //do nothing
            } else {
                // if status is false - not connected
                Snackbar.make(
                    mainActivityBinding.root,
                    NO_INTERNET,
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(RETRY_BUTTON, { isNetworkAvailable() })
                    setActionTextColor(getColor(R.color.black))
                }.show()
            }
        })
    }
}