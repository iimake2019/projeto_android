package com.example.imake.imake.Activity.Activity

import android.content.Intent
import android.os.Bundle

import com.bumptech.glide.Glide
import com.example.imake.R
import com.example.imake.imake.Activity.Entidades.Usuario
import com.google.android.material.floatingactionbutton.FloatingActionButton

import android.view.View
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle

import android.view.MenuItem

import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import androidx.drawerlayout.widget.DrawerLayout

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.view.Menu
import android.webkit.WebView
import android.widget.TextView

import de.hdodenhof.circleimageview.CircleImageView

class DrawerNavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var navigationView: NavigationView
    private lateinit var drawer: DrawerLayout
    private var toolbar: Toolbar? = null

    private var bd: FirebaseDatabase? = null
    private var referenceBd: DatabaseReference? = null

    private lateinit var nav_perfil: MenuItem
    private lateinit var nav_sobre: MenuItem
    private lateinit var nav_desejos: MenuItem
    private lateinit var nav_favoritos: MenuItem
    private lateinit var nav_home: MenuItem
    private lateinit var nav_categorias: MenuItem
    private lateinit var itemB: MenuItem
    private lateinit var categorias2: MenuItem
    private var nav_voltar: MenuItem? = null
    private lateinit var nomeUsuarioLogado: TextView
    private var u: Usuario? = null

    private lateinit var imgHeader: CircleImageView
    private lateinit var codigoSessao: String
    private lateinit var fab : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_navigation)

        inicializarFirebase()
        val wv = findViewById(R.id.wv) as WebView

        wv.settings.javaScriptEnabled = true

        // Get the Android assets folder path
        val folderPath = "file:android_asset/"

        // Get the HTML file name
        val fileName = "telaAndroid.html"

        // Get the exact file location
        val file = folderPath + fileName

        /*
                    loadUrl(String url)
                        Loads the given URL.
                 */

        // Render the HTML file on WebView
        wv.loadUrl(file)

        //NAVIGATION DRAWER
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        fab = findViewById(R.id.fab)
        fab.setOnClickListener(View.OnClickListener {
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/
            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@DrawerNavigationActivity, MessagesActivity::class.java)
            startActivity(intent)
        })
        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view) as NavigationView
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        val intent = this.intent
        val bundle = intent.extras
        codigoSessao = bundle!!.getSerializable("codigoUsuario") as String
        bundle.clear()


        //HEADER
        val hView = navigationView.getHeaderView(0)
        nomeUsuarioLogado = hView.findViewById(R.id.txtNomeUsuarioLogado) as TextView
        imgHeader = hView.findViewById(R.id.imgHeader) as CircleImageView

        pesquisarInfo()
        fab.setOnClickListener(View.OnClickListener {
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/
            val bundle = Bundle()
            bundle.putString("codigoSessao", codigoSessao)
            val intent = Intent(this@DrawerNavigationActivity, MessagesActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        })

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
        menuInflater.inflate(R.menu.drawer_navigation, menu)
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
            if (Integer.valueOf(u!!.tipoUsuario) == 2) {

                val bundle = Bundle()
                bundle.putString("codigoUsuario", u!!.codigo)
                val intent = Intent(this@DrawerNavigationActivity, ProfActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            } else {
                val bundle = Bundle()
                bundle.putString("codigoUsuario", u!!.codigo)
                val intent = Intent(this@DrawerNavigationActivity, AmadorActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        } else if (id == R.id.nav_sobre) {
            // Handle the camera action
            val intent = Intent(this@DrawerNavigationActivity, QuemSomosActivity::class.java)
            startActivity(intent)

        } else if (id == R.id.nav_pesquisar) {

            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@DrawerNavigationActivity, PesquisarActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        } else if (id == R.id.nav_desejos) {
            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@DrawerNavigationActivity, DesejoActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        } else if (id == R.id.nav_favoritos) {
            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@DrawerNavigationActivity, FavoritosActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        } else if (id == R.id.nav_home) {
            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@DrawerNavigationActivity, FeedGeralActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

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
        } else if (id == R.id.nav_cat_artistico) {
            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@DrawerNavigationActivity, ArtisticoActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        } else if (id == R.id.nav_cat_casamento) {
            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@DrawerNavigationActivity, CasamentoActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        } else if (id == R.id.nav_cat_debutante) {
            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@DrawerNavigationActivity, DebutanteActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        } else if (id == R.id.nav_cat_festa) {
            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@DrawerNavigationActivity, FestaActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        } else if (id == R.id.nav_cat_formatura) {
            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@DrawerNavigationActivity, FormaturaActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
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
            val intent = Intent(this@DrawerNavigationActivity, SplashActivity::class.java)
            startActivity(intent)
            finish()
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

    fun pesquisarInfo() {
        val q = referenceBd!!.child("Usuario").child(codigoSessao)
        q.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    u = dataSnapshot.getValue(Usuario::class.java)
                    nomeUsuarioLogado.text = u!!.nome
                    if (u!!.urlPerfil != null) {
                        Glide.with(applicationContext).load(u!!.urlPerfil).into(imgHeader)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@DrawerNavigationActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
    }

    /* @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }*/
}
