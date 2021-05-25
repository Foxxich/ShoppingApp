package com.example.shoppingapp.details

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.shoppingapp.model.ProductsModel
import com.example.shoppingapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class ProductDetailsActivity : AppCompatActivity() {

    private var item: ProductsModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        val message = intent.getStringExtra("itemToShow")
        FirebaseDatabase.getInstance().getReference("Game").child(message!!).addValueEventListener(object : ValueEventListener {

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
    }

    private fun prepareActivityWithProductDetails(itemData: ProductsModel) {
        Glide.with(this@ProductDetailsActivity)
                .load(itemData.image)
                .into(findViewById(R.id.itemImageView))
        findViewById<TextView>(R.id.itemNameTextView).text = itemData.name
        findViewById<TextView>(R.id.itemPriceTextView).text = itemData.price + " zł"
        findViewById<TextView>(R.id.itemDetailsTextView).text = itemData.description
    }

    fun addToCart(view: View) { //item
// TODO: Ilya - add to cart
    }
}