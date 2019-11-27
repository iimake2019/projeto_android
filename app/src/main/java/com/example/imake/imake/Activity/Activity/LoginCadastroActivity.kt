package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.example.imake.R
import com.example.imake.imake.Activity.DAO.ConfiguracaoFirebase
import com.example.imake.imake.Activity.Entidades.Usuario
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginCadastroActivity : AppCompatActivity() {


    private val GOOGLE_SIGN_IN = 123
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var btnRegistrarGoogle: Button

    private lateinit var txtEsqueciSenha: TextView
    private lateinit var txtCadastro: TextView
    private lateinit var edtUsuario: EditText
    private lateinit var edtSenha: EditText
    private lateinit var btnLogar: Button

    private lateinit var autenticacao : FirebaseAuth
    private lateinit var bd: FirebaseDatabase
    private lateinit var referenceBD: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_cadastro)
        supportActionBar!!.hide()

        inicializarFirebase()



        txtCadastro = findViewById(R.id.txtCadastro) as TextView
        edtUsuario = findViewById(R.id.edtUsuario) as EditText
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha) as TextView
        edtSenha = findViewById(R.id.edtSenha) as EditText
        btnLogar = findViewById(R.id.btnLogar) as Button
        btnRegistrarGoogle = findViewById(R.id.btnRegistrarGoogle) as Button

        //CADASTRO GOOGLE

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this@LoginCadastroActivity, gso)

        btnRegistrarGoogle.setOnClickListener { SignInGoogle() }

        if (mAuth.currentUser != null) {
            val user = mAuth.currentUser
            updateUI(user)
        }

        btnLogar.setOnClickListener {
            if (edtUsuario.text.toString() != "" && edtUsuario.text.toString() != null && edtSenha.text.toString() != "" && edtSenha.text.toString() != null) {
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
                autenticacao.signInWithEmailAndPassword(edtUsuario.text.toString(), edtSenha.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val c = Usuario()
                        c.email = edtUsuario.text.toString()
                        val q = referenceBD.child("Usuario").child(c.gerarCodigo(c.email))
                        q.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                var u: Usuario? = Usuario()
                                u!!.codigo = "Teste"
                                u = dataSnapshot.getValue(Usuario::class.java)

                                val bundle = Bundle()
                                var verif = 1
                                if (u == null) {
                                    u = Usuario()
                                    u.codigo = "Teste"
                                    verif = 0
                                }
                                bundle.putString("codigoUsuario", u.codigo)
                                val intent = Intent(this@LoginCadastroActivity, DrawerNavigationActivity::class.java)
                                intent.putExtras(bundle)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                if (verif == 0) {
                                    Toast.makeText(this@LoginCadastroActivity, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        })
                    } else {
                        var erroExcecao = ""
                        try {
                            throw task.exception as java.lang.Exception
                        } catch (e: FirebaseAuthWeakPasswordException) {
                            erroExcecao = "Digite uma senha mais forte, contendo no minimo 6 caracteres."
                        } catch (e: FirebaseAuthEmailException) {
                            erroExcecao = "Email incorreto. Por favor, digite um e-mail válido."
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            erroExcecao = "Senha incorreta. Por favor, digite a senha correta."
                        } catch (e: FirebaseNetworkException) {
                            erroExcecao = "Sem conexão com a internet. Tente novamente."
                        } catch (e: FirebaseAuthInvalidUserException) {
                            erroExcecao = "Usuário não cadastrado. Por favor, tente com um usuário válido."
                        } catch (e: Exception) {
                            erroExcecao = "Não foi possível fazer o login. Tente novamente mais tarde."
                            e.printStackTrace()
                        }

                        Toast.makeText(this@LoginCadastroActivity, "Erro: $erroExcecao", Toast.LENGTH_SHORT).show()
                    }
                }


            } else {
                Toast.makeText(this@LoginCadastroActivity, "Preencha os campos de e-mail e senha!", Toast.LENGTH_SHORT).show()
            }
        }

        txtCadastro.setOnClickListener {
            val intent = Intent(this@LoginCadastroActivity, CadastroActivity::class.java)
            startActivity(intent)
        }
        txtEsqueciSenha.setOnClickListener {
            val intent = Intent(this@LoginCadastroActivity, EsqueciSenhaActivity::class.java)
            startActivity(intent)
        }
        Logout()
    }

    //CADASTRO GOOGLE
    fun SignInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "signInWithCredential:success")

                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.exception)

                        Toast.makeText(this@LoginCadastroActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("TAG", "Google sign in failed", e)
            }

        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val name = user.displayName
            val email = user.email
            val photo = user.photoUrl.toString()
            val bundle = Bundle()
            bundle.putString("emailUsuario", email)
            bundle.putString("nomeUsuario", name)
            bundle.putString("urlFoto", photo)
            val intent = Intent(this@LoginCadastroActivity, CadastroActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            //Logout();
        }
    }

    private fun Logout() {
        FirebaseAuth.getInstance().signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUI(null)
            }
        }
    }

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@LoginCadastroActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBD = bd.getReference()
    }
}
