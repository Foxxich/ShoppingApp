package com.example.shoppingapp.details

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppingapp.model.ProductsModel
import com.example.shoppingapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class ProductDetailsActivity : AppCompatActivity() {
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
                            //TODO: handle with date on our activity!
                            Picasso.get().load(itemData.image).into(findViewById<ImageView>(
                                R.id.itemImageView
                            ))
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
}