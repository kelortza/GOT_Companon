package com.e.got_compagnon

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    //al profe no le gusta esta forma de trabajar asike beste modu batean egiten badut no pasa nada baiana modu hau ezagutu behar dut
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    //Views
    private lateinit var emailEdit: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Initialize Firebase Auth
        auth = Firebase.auth
        //Initialize Firebase firestore
        firestore = Firebase.firestore

        //Init views
        initViews()

        //Init Listeners
        initListeners()
    }

    private fun initViews() {
        passwordEditText = findViewById<EditText>(R.id.passwordLogin)
        emailEdit = findViewById<EditText>(R.id.emailLogin)
        loginButton = findViewById<Button>(R.id.loginButton)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
    }

    private fun initListeners() {
        loginButton.setOnClickListener {
            val username = emailEdit.text.toString()
            //username not empty
            //se podria hacer una funcion que no solo mira si esta vacio pero tambien si hay palabras prohibidas
            if (username.isBlank()) {
                emailEdit.error = "Username cannot be empty"
                return@setOnClickListener
            }
            val password = passwordEditText.text.toString()
            //register user
            signIn(username, password)
        }
    }

    private fun signIn(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        loginButton.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE
                        loginButton.isEnabled = true
                        finish()
                    } else {
                        //Handle error
                        Log.i(TAG, "Error: ${task.exception}")
                        showMessage("Error signing up ${task.exception?.message ?: ""}")
                        //hide loading
                        progressBar.visibility = View.GONE
                    }
                })
    }

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}

