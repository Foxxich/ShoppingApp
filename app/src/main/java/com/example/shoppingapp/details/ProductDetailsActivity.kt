package com.example.shoppingapp.details

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.shoppingapp.MainActivity
import com.example.shoppingapp.R
import com.example.shoppingapp.menu_activities.UserData
import com.example.shoppingapp.model.CartModel
import com.example.shoppingapp.model.ProductsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*


class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var userName: String
    private lateinit var message: String
    private var item: ProductsModel? = null
    private var cartModel: CartModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        message = intent.getStringExtra("itemToShow").toString()
        userName = intent.getStringExtra("user").toString()
        currentFireBase()
    }

    private fun currentFireBase() {
        FirebaseDatabase.getInstance().getReference("Game").child(message).addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                Log.e("onCancelled", " cancelled")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val itemData = snapshot.getValue<ProductsModel>(ProductsModel::class.java)
                    if (itemData != null) {
                        try {
                            item = itemData
                            prepareActivityWithProductDetails(itemData)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.e("TAG", " it's null.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        FirebaseDatabase.getInstance().getReference("Cart").child(userName).addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                Log.e("onCancelled", " cancelled")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    cartModel = snapshot.getValue<CartModel>(CartModel::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun prepareActivityWithProductDetails(itemData: ProductsModel) {
        val itemImageView = findViewById<ImageView>(R.id.itemImageView)
        Picasso.get().load(itemData.image).into(itemImageView)
        findViewById<TextView>(R.id.itemNameTextView).text = itemData.name
        findViewById<TextView>(R.id.itemPriceTextView).text = itemData.price + " zł"
        findViewById<TextView>(R.id.itemDetailsTextView).text = itemData.description
    }

    fun addToCart(view: View) {
        databaseAdd()
    }

    private fun databaseAdd() {
        cartModel!!.quantity++
        cartModel!!.totalPrice += item!!.price!!.toFloat()
        FirebaseDatabase.getInstance().getReference("Cart").child(userName).setValue(cartModel)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("userName", userName)
        }
        startActivity(intent)
        finish()
    }

}