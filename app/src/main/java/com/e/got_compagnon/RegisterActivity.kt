package com.e.got_compagnon

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.e.got_compagnon.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private val TAG = "RegisterActivity"

    //al profe no le gusta esta forma de trabajar asike beste modu batean egiten badut no pasa nada baiana modu hau ezagutu behar dut
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val MIN_PASSWORD_LENGTH = 6

    //Views
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
        emailEditText = findViewById<EditText>(R.id.emailEditText)
        passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        registerButton = findViewById<Button>(R.id.registerButton)
        usernameEditText = findViewById(R.id.usernameEditText)
        progressBar = findViewById((R.id.progressBar))
    }

    private fun initListeners() {
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            //username not empty
            //se podria hacer una funcion que no solo mira si esta vacio pero tambien si hay palabras prohibidas
            if (username.isBlank()) {
                usernameEditText.error = "Username cannot be empty"
                return@setOnClickListener
            }
            val email = emailEditText.text.toString()
            //validate email is valid
            if (!isEmailValid(email)) {
                Log.i(TAG, "Email not valid")
                //showMessage(getString(R.string.error_email_invalid))
                //Otro modo de mostrar errores
                emailEditText.error = getString(R.string.error_email_invalid)
                return@setOnClickListener
            }
            val password = passwordEditText.text.toString()
            //validate password
            if (!isPasswordValid(password)) {
                Log.i(TAG, "Password not valid")
                //showMessage(getString(R.string.error_password_invalid))
                //otro metodo para enseñar errores
                passwordEditText.error =
                    getString(R.string.error_password_invalid, MIN_PASSWORD_LENGTH)
                return@setOnClickListener
            }
            //register user
            registerUser(email, password, username)
        }
    }

    private fun registerUser(email: String, password: String, username: String) {
        //show loading bar
        progressBar.visibility = View.VISIBLE
        registerButton.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //TODO: User registered
                    Log.i(TAG, "User Registered!")
                    //TODO: create user into the data base [userID + username]
                    auth.currentUser?.uid?.let { userID ->
                        //create user model
                        val user = User(userId = userID, username = username)
                        //set document to firestore
                        firestore
                            .collection(Constants.COLLECTION_USERS)
                            .document(userID)
                            .set(user)
                            .addOnCompleteListener {
                                progressBar.visibility = View.GONE
                                registerButton.isEnabled = true
                                finish()
                                if (it.isSuccessful) {
                                    //TODO: finish
                                    Log.i(TAG, "User Profile Created")
                                } else {
                                    Log.w(TAG, "User Profile Error")
                                }
                            }
                    } ?: kotlin.run {
                        Log.i(TAG, "Error: ${it.exception}")
                        showMessage("Error signing up ${it.exception?.message ?: ""}")
                        //Hide loading
                        progressBar.visibility = View.GONE
                        registerButton.isEnabled = true
                    }
                } else {
                    //Handle error
                    Log.i(TAG, "Error: ${it.exception}")
                    showMessage("Error signing up ${it.exception?.message ?: ""}")
                    //hide loading
                    progressBar.visibility = View.GONE
                }
            }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
        return email.isNotBlank()
                && email.contains("@")
                && email.contains(Regex(emailRegex))
    }

    private fun isPasswordValid(password: String): Boolean {
        //not empty
        return password.isNotBlank()
                //min 6 characters
                && password.count() >= MIN_PASSWORD_LENGTH
                //contains letters & numbers
                && containsLetterAndNumber(password)
    }

    private fun containsLetterAndNumber(text: String): Boolean {
        var containsLetter = false
        var containsNumber = false
        //los dos tipos de for que existen en kotlin
        text.forEach {
            if (it.isDigit()) {
                containsNumber = true
            }
            if (it.isLetter()) {
                containsLetter = true
            }
        }

        return containsLetter && containsNumber
        /*
        for(char: Char in text){

        }
        */
    }

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}