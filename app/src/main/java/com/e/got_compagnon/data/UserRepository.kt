package com.e.got_compagnon.data

import android.content.Context
import com.e.got_compagnon.model.User

class UserRepository (
    private val firestoreDataSource: UserFirestoreDataSource = UserFirestoreDataSource(),
    private val localDataSource: UserLocalDataSource = UserLocalDataSource()
    ){

        //tries to get username locally first
        //if not available, get from firestore
        //and save locally

        fun getUsername(context: Context, userId: String, resultListener: ((String?) -> Unit)){
            localDataSource.getUsername(context)?.let {username ->
                //return username
                resultListener(username)
            }?: run {
                //get from firestore
                firestoreDataSource.getUser(userId) {user: User? ->
                    //save locally
                    localDataSource.saveUsername(context, user?.username ?: "")
                    //return result
                    resultListener(user?.username)
                }
            }
        }
}