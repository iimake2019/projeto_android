package com.example.imake.imake.Activity.Activity

import android.content.Intent
import android.os.Bundle

import com.example.imake.R

import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle

import android.view.MenuItem

import com.google.android.material.navigation.NavigationView

import androidx.drawerlayout.widget.DrawerLayout

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.view.Menu
import android.widget.TextView

class TesteNavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var nav_perfil: MenuItem
    private lateinit var nav_sobre: MenuItem
    private lateinit var nav_desejos: MenuItem
    private lateinit var nav_favoritos: MenuItem
    private lateinit var nav_home: MenuItem
    private lateinit var nav_categorias: MenuItem
    private lateinit var itemB: MenuItem
    private lateinit var categorias2: MenuItem
    private var nav_voltar: MenuItem? = null
    private var nomeUsuarioLogado: TextView? = null

    private lateinit var navigationView: NavigationView
    private lateinit var drawer: DrawerLayout
    private  var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teste_navigation)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        drawer = findViewById(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_drawer_navigation_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_perfil) {
            val intent = Intent(this@TesteNavigationActivity, PerfilAmadorActivity::class.java)
            startActivity(intent)

        } else if (id == R.id.nav_sobre) {
            // Handle the camera action
            val intent = Intent(this@TesteNavigationActivity, QuemSomosActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.nav_desejos) {

        } else if (id == R.id.nav_favoritos) {

        } else if (id == R.id.nav_home) {


        } else if (id == R.id.nav_categorias) {
            inicializaNav()
            nav_perfil.isVisible = false
            nav_sobre.isVisible = false
            nav_desejos.isVisible = false
            nav_favoritos.isVisible = false
            nav_home.isVisible = false
            nav_categorias.isVisible = false
            categorias2.isVisible = true
            itemB.isVisible = false
        } else if (id == R.id.nav_voltar) {
            nav_perfil.isVisible = true
            nav_sobre.isVisible = true
            nav_desejos.isVisible = true
            nav_favoritos.isVisible = true
            nav_home.isVisible = true
            nav_categorias.isVisible = true
            categorias2.isVisible = false
            itemB.isVisible = true
        } else if (id == R.id.nav_logout) {
            val intent = Intent(this@TesteNavigationActivity, SplashActivity::class.java)
            startActivity(intent)
        }


        drawer = findViewById(R.id.drawer_layout)
        //drawer.closeDrawer(GravityCompat.START);
        return true
    }

    private fun inicializaNav() {
        val menuNav = navigationView.menu
        nav_perfil = menuNav.findItem(R.id.nav_perfil)
        nav_sobre = menuNav.findItem(R.id.nav_sobre)
        nav_desejos = menuNav.findItem(R.id.nav_desejos)
        nav_favoritos = menuNav.findItem(R.id.nav_favoritos)
        nav_home = menuNav.findItem(R.id.nav_home)
        nav_categorias = menuNav.findItem(R.id.nav_categorias)
        categorias2 = menuNav.findItem(R.id.nav_categorias2)
        itemB = menuNav.findItem(R.id.itemB)


    }
}