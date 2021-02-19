package com.e.got_compagnon

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.e.got_compagnon.fragment.ChatFragment
import com.e.got_compagnon.fragment.NewsFragment
import com.e.got_compagnon.fragment.ProfileFragment
import com.e.got_compagnon.fragment.StreamsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //find views
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        //get Fragment Container
        val fragmentContainer: FrameLayout =
            findViewById(R.id.fragmentContainer)
        //Listen to Tabs Selected usando un listenner
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.chat ->{
                    //TODO: Chat
                    val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, ChatFragment())
                    transaction.commit()
                }
                R.id.news ->{
                    //TODO News
                    val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, NewsFragment())
                    transaction.commit()
                }
                R.id.perfil ->{
                    //TODO Perfil
                    val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, ProfileFragment())
                    transaction.commit()
                }
                R.id.twicth ->{
                    val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, StreamsFragment())
                    transaction.commit()
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        bottomNavigationView.selectedItemId = R.id.news
    }
}