package com.e.got_compagnon.data

import com.e.got_compagnon.Constants.COLLECTION_USERS
import com.e.got_compagnon.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserFirestoreDataSource {
    fun getUser(userId: String, resultListener: ((User?) -> Unit)) {
        Firebase.firestore
            .collection(COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnCompleteListener{
                if(it.isSuccessful){
                    val user = it.result?.toObject(User::class.java)
                    resultListener(user)
                }else{
                    resultListener(null)
                }
            }
    }
}