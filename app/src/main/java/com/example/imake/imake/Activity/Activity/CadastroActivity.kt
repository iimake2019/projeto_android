package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.example.imake.R
import com.example.imake.imake.Activity.DAO.ConfiguracaoFirebase
import com.example.imake.imake.Activity.Entidades.Usuario
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CadastroActivity : AppCompatActivity() {
    private lateinit var edtNome: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtCpf: EditText
    private lateinit var edtTelefone: EditText
    private lateinit var edtEndereco: EditText
    private lateinit var edtCidade: EditText
    private lateinit var edtIdade: EditText
    private lateinit var edtSenha: EditText
    private lateinit var edtConfirmarSenha: EditText
    private lateinit var btnCadastrar: Button
    private lateinit var spnCategorias: Spinner
    private lateinit var rbNormal: RadioButton
    private lateinit var rbProf: RadioButton
    private lateinit var txtCategoria: TextView
    private var usuarios: Usuario? = null

    private var email: String? = null
    private lateinit var name: String
    private lateinit var urlFoto: String
    private  var autenticacao: FirebaseAuth? = null
    private lateinit var mAuth: FirebaseAuth
    private var bd: FirebaseDatabase? = null
    private var referenceBd: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_cadastro)

        val intent = this.intent
        if (intent.extras != null) {
            val bundle = intent.extras
            email = bundle!!.getSerializable("emailUsuario") as String
            name = bundle.getSerializable("nomeUsuario") as String
            urlFoto = bundle.getSerializable("urlFoto") as String
            bundle.clear()
        }

        mAuth = FirebaseAuth.getInstance()
        inicializarComponentes()
        inicializarFirebase()
        rbNormal.setOnClickListener {
            spnCategorias.visibility = View.INVISIBLE
            txtCategoria.visibility = View.INVISIBLE
        }
        rbProf.setOnClickListener {
            spnCategorias.visibility = View.VISIBLE
            txtCategoria.visibility = View.VISIBLE
        }
        btnCadastrar.setOnClickListener {
            if (edtConfirmarSenha.text.toString() == edtSenha.text.toString() && edtNome.text.toString() != "" && edtEmail.text.toString() != "" && edtCpf.text.toString() != ""
                    && edtTelefone.text.toString() != "" && edtEndereco.text.toString() != "" && edtCidade.text.toString() != ""
                    && edtIdade.text.toString() != "" && edtSenha.text.toString() != "") {
                usuarios = Usuario()
                if (email != null) {
                    usuarios!!.urlPerfil = urlFoto
                }
                if (spnCategorias.selectedItemPosition == 0) {
                    usuarios!!.categoria = "Casamento"
                } else if (spnCategorias.selectedItemPosition == 1) {
                    usuarios!!.categoria = "Debutante"
                } else if (spnCategorias.selectedItemPosition == 2) {
                    usuarios!!.categoria = "Festa"
                } else if (spnCategorias.selectedItemPosition == 3) {
                    usuarios!!.categoria = "Formatura"
                } else if (spnCategorias.selectedItemPosition == 4) {
                    usuarios!!.categoria = "Artistico"
                }
                if (rbNormal.isChecked) {
                    usuarios!!.tipoUsuario = "1"
                    usuarios!!.categoria = null
                } else if (rbProf.isChecked) {
                    usuarios!!.tipoUsuario = "2"
                }
                usuarios!!.numeroMakes = "0"
                usuarios!!.nome = edtNome.text.toString()
                usuarios!!.email = edtEmail.text.toString()
                usuarios!!.cpf = edtCpf.text.toString()
                usuarios!!.telefone = edtTelefone.text.toString()
                usuarios!!.endereco = edtEndereco.text.toString()
                usuarios!!.cidade = edtCidade.text.toString()
                usuarios!!.idade = edtIdade.text.toString()
                usuarios!!.senha = edtSenha.text.toString()
                usuarios!!.avaliacao = "0"
                usuarios!!.nAvaliacao = "0"

                if (email != null) {
                    val user = mAuth.currentUser
                    user!!.updatePassword(usuarios!!.senha)
                }


                usuarios!!.codigo = usuarios!!.gerarCodigo(usuarios!!.email)

                referenceBd!!.child("Usuario").child(usuarios!!.codigo).setValue(usuarios)
                cadastrarUsuario()
            } else {
                Toast.makeText(this@CadastroActivity, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cadastrarUsuario() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao()
        autenticacao!!.createUserWithEmailAndPassword(
                usuarios!!.email,
                usuarios!!.senha
        ).addOnCompleteListener(this@CadastroActivity) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@CadastroActivity, "Usuario cadastrado com sucesso.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@CadastroActivity, SplashActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                var verif = 0
                var erroExcecao = ""

                try {
                    throw task.getException() as Exception
                } catch (e: FirebaseAuthWeakPasswordException) {
                    erroExcecao = "Digite uma senha mais forte, contendo no minimo 6 caracteres"
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    erroExcecao = "O email digitado é inválido, digite um novo email"
                } catch (e: FirebaseAuthUserCollisionException) {
                    if (email == null) {
                        erroExcecao = "Esse email ja esta cadastrado no sistema"
                    } else {
                        verif = 1
                        Toast.makeText(this@CadastroActivity, "Usuario cadastrado com sucesso.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@CadastroActivity, SplashActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                } catch (e: Exception) {
                    erroExcecao = "Erro ao efetuar o cadastro"
                    e.printStackTrace()
                }

                if (verif == 0) {
                    Toast.makeText(this@CadastroActivity, "Erro: $erroExcecao", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun inicializarComponentes() {
        edtNome = findViewById(R.id.edtNome) as EditText
        edtEmail = findViewById(R.id.edtEmail) as EditText
        edtCpf = findViewById(R.id.edtCPF) as EditText
        edtTelefone = findViewById(R.id.edtTelefone) as EditText
        edtEndereco = findViewById(R.id.edtEndereco) as EditText
        edtCidade = findViewById(R.id.edtCidade) as EditText
        edtIdade = findViewById(R.id.edtIdade) as EditText
        edtSenha = findViewById(R.id.edtSenha) as EditText
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha) as EditText
        btnCadastrar = findViewById(R.id.btnCadastrar) as Button
        spnCategorias = findViewById(R.id.spnCategorias)
        txtCategoria = findViewById(R.id.txtCategoria) as TextView
        rbNormal = findViewById(R.id.rbNormal) as RadioButton
        rbProf = findViewById(R.id.rbProf) as RadioButton
        val adapter = ArrayAdapter.createFromResource(this, R.array.categoria, R.layout.spinner_item)
        spnCategorias.adapter = adapter
        if (email != null) {
            edtEmail.setText(email)
            edtNome.setText(name)
        }
    }

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@CadastroActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
    }
}
