package com.example.shoppingapp.account

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.shoppingapp.MainActivity
import com.example.shoppingapp.R

class LoginActivity : AppCompatActivity() {

    private val MY_PREFS_NAME = "USER"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val prefs =  getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE) //check if user logged before
        val name =
            prefs.getString("name", "No name defined") //"No name defined" is the default value.
        if(name != null && name != "No name defined") { //case if user logged before
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("userName", name)
            }
            startActivity(intent)
        }
    }

    /*
     * Log to the account
     */
    fun loginToAccount(view: View) {
        val intent = Intent(this, AccountActivity::class.java).apply {
            putExtra("openAccount","login")
        }
        startActivity(intent)
    }

    /*
     * Register new account
     */
    fun registerAccount(view: View) {
        val intent = Intent(this, AccountActivity::class.java).apply {
            putExtra("openAccount","register")
        }
        startActivity(intent)
    }
}