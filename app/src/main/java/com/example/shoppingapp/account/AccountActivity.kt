package com.example.shoppingapp.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppingapp.MainActivity
import com.example.shoppingapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase


class AccountActivity : AppCompatActivity() {

    private lateinit var activationType : String
    var databaseReference: DatabaseReference? = null
    private lateinit var auth: FirebaseAuth

    private var accountName : String = ""
    private var accountEmail : String = ""
    private var accountPassword : String = ""

    private val MY_PREFS_NAME = "USER"
    private val TAG = "DDF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        activationType = intent.getStringExtra("openAccount").toString()
        correctScreen()
        auth = Firebase.auth
    }

    fun logToChat(view: View) {
        accountEmail = findViewById<EditText>(R.id.emailEditText).text.toString()
        accountPassword = findViewById<EditText>(R.id.passwordEditText).text.toString()
        if(activationType == "register"){
            accountName =  findViewById<EditText>(R.id.nameEditText).text.toString()
            createAccount(accountEmail, accountPassword,accountName)
        } else {
            signInAccount(accountEmail, accountPassword)
        }
    }

    private fun createAccount(email: String, password: String, name : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    user!!.updateProfile(profileUpdates).addOnCompleteListener(this) { insertName ->
                        if(insertName.isSuccessful){
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun signInAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    if (user != null) {
                        accountName = if(user.displayName != null) {
                            user?.displayName.toString()
                        } else {
                            user?.email.toString()
                        }
                    }
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    //Change UI according to user data.
    private fun updateUI(account: FirebaseUser?){
        if (account != null) {
            //Toast.makeText(this, "U Signed In successfully", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("userName", accountName)
            }
            val editor =
                getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()
            editor.putString("name", accountName)
            editor.putString("pass", accountPassword)
            editor.putString("email", accountEmail)
            editor.apply()
            startActivity(intent)
        } else {
            Toast.makeText(this, "U Didn't signed in", Toast.LENGTH_LONG).show()
        }
    }

    private fun correctScreen() {
        if(activationType == "login"){
            findViewById<EditText>(R.id.nameEditText).visibility = View.INVISIBLE
        }
    }
}