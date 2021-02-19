package com.e.got_compagnon.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.e.got_compagnon.Constants
import com.e.got_compagnon.Constants.COLLECTION_USERS
import com.e.got_compagnon.LoginActivity
import com.e.got_compagnon.R
import com.e.got_compagnon.RegisterActivity
import com.e.got_compagnon.model.User
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class ProfileFragment: Fragment() {

    val CHANGE_PICTURE_REQUEST = 0
    private lateinit var selectedImageBytes: ByteArray

    private val TAG = "ProfileFragment"

    private var storageReference = FirebaseStorage.getInstance().getReference()
    private lateinit var firestore: FirebaseFirestore
    private lateinit  var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var profilePicture: ImageView
    //private lateinit var changePicureButton: Button
    private lateinit var logoutButton: Button
    private lateinit var usernameTextView: TextView
    private var pictureJustChanged = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "++ onCreateView ++")
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "++ onViewCreated ++")
        firestore = Firebase.firestore
        //Init views
        initViews(view)
        //Init Listeners
        initListeners()
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "++ onStart ++")
        checkUserAvailability()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "++ onResume ++")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "++ onPause ++")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "++ onStop ++")
    }

    private fun initViews(parentView: View){
        registerButton = parentView.findViewById<Button>(R.id.registerButton)
        loginButton = parentView.findViewById<Button>(R.id.loginButton)
        profilePicture = parentView.findViewById<ImageView>(R.id.profilePicture)
        profilePicture.visibility = View.GONE
        //changePicureButton = parentView.findViewById<Button>(R.id.changePictureButton)
        //changePicureButton.visibility = View.GONE
        usernameTextView = parentView.findViewById(R.id.usernameTextView)
        usernameTextView.visibility = View.GONE
        logoutButton = parentView.findViewById(R.id.logoutButton)
        logoutButton.visibility = View.GONE
    }

    private fun initListeners(){
        registerButton.setOnClickListener(){
            Firebase.analytics.logEvent("registerButtonClick", null)
            //TODO: Track register button click
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener(){
            Firebase.analytics.logEvent("loginButtonClick", null)
            //TODO: Track register button click
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        profilePicture.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                CHANGE_PICTURE_REQUEST
            )
        }

        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            //TODO: verificar si han habido errores o no

            registerButton.visibility = View.VISIBLE
            loginButton.visibility = View.VISIBLE
            profilePicture.visibility = View.GONE
            usernameTextView.visibility = View.GONE
            logoutButton.visibility = View.GONE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHANGE_PICTURE_REQUEST && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                .getBitmap(activity?.contentResolver, selectedImagePath)

            //todo esto se podria hacer en otra funcion una vez que se ha subido la imagen al firebase
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()
            //profilePicture.setImageBitmap(selectedImageBmp)
            pictureJustChanged = true
            Glide.with(this)
                .load(selectedImageBmp)
                .into(profilePicture)
            uploadImageToFirebase(selectedImagePath!!)
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri){
        if(fileUri != null) {
            val user = Firebase.auth.currentUser
            val imageRef = storageReference.child("images/" + user!!.uid + "/" + fileUri.lastPathSegment)
            val uploadTask = imageRef.putFile(fileUri)
            uploadTask.addOnSuccessListener {
                val downloadUrl = imageRef.downloadUrl
                downloadUrl.addOnSuccessListener {
                    firestore.collection(Constants.COLLECTION_USERS)
                        .document(user!!.uid)
                        .update("profilePicturePath", it.toString())
                }
            }
            uploadTask.addOnFailureListener{
                //cuidado con esto
                Log.e(TAG, it.message!!)
            }
        }
    }


    //Update UI depending if user is available or not
    private fun checkUserAvailability() {
        //con esta linea miramos si el Firebase ya esta conectado o no (si hay un usuario guardado o no
        //lo bueno es que no hace falta ningun listener ya que to-do eso esta guardado en local dentro del movil
        Firebase.auth.currentUser?.let {
            //User available
            registerButton.visibility = View.GONE
            loginButton.visibility = View.GONE
            profilePicture.visibility = View.VISIBLE
            //changePicureButton.visibility = View.VISIBLE
            usernameTextView.visibility = View.VISIBLE
            logoutButton.visibility = View.VISIBLE

            firestore.collection(Constants.COLLECTION_USERS)
                .document(it.uid)
                .get()
                .addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        it.result?.toObject(User::class.java)
                            ?.let { user: User ->
                                usernameTextView.text = user.username
                                if(!user.profilePicturePath.isNullOrBlank())
                                {
                                    //TODO: cargar la imagen del url en el imageView
                                    Glide.with(this)
                                        .load(user.profilePicturePath)
                                        .into(profilePicture)
                                }
                            }
                    }
                }

        } ?: run {
            //User not available
            registerButton.visibility = View.VISIBLE
            loginButton.visibility = View.VISIBLE
            profilePicture.visibility = View.GONE
            logoutButton.visibility = View.GONE
            usernameTextView.visibility = View.GONE
            //changePicureButton.visibility = View.GONE
        }
    }
}