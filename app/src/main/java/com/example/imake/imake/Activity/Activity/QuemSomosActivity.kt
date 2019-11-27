package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout

import android.os.Bundle
import android.webkit.WebView

import com.example.imake.R
import com.google.android.material.navigation.NavigationView

class QuemSomosActivity : AppCompatActivity() {
    internal var navigationView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quem_somos)


        val wv = findViewById(R.id.wv) as WebView

        wv.settings.javaScriptEnabled = true

        // Get the Android assets folder path
        val folderPath = "file:android_asset/"

        // Get the HTML file name
        val fileName = "sobre.html"

        // Get the exact file location
        val file = folderPath + fileName

        /*
                    loadUrl(String url)
                        Loads the given URL.
                 */

        // Render the HTML file on WebView
        wv.loadUrl(file)

    }
}
