package com.example.shoppingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.model.ProductsModel
import com.example.shoppingapp.eventbus.UpdateCartEvent
import com.example.shoppingapp.listener.ICartLoadListener
import com.example.shoppingapp.listener.ItemListener
import com.example.shoppingapp.model.CartModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nex3z.notificationbadge.NotificationBadge
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class ProductsAdapter(private val context: Context, private val list: List<ProductsModel>, private val cartListener: ICartLoadListener, private val itemListener: ItemListener, private val userName: String) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    inner class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = null
        var txtName: TextView? = null
        var txtPrice: TextView? = null

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
            itemView.findViewById<LinearLayout>(R.id.game_item_layout).setOnClickListener {
                itemListener.clickedLong(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_game_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        Glide.with(context)
                .load(list[position].image)
                .into(holder.imageView!!)
        holder.txtName!!.text  = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text  = StringBuilder().append(list[position].price+" zł")
    }

    //TODO:insert this method to UserData.class and change it!
    private fun addToCart(productsModel: ProductsModel) {

        val userCart = FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(userName)

        userCart.child(productsModel.key!!)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()) {
                            val cartModel = snapshot.getValue(CartModel::class.java)
                            val updateData: MutableMap<String,Any> = HashMap()
                            cartModel!!.quantity = cartModel.quantity + 1
                            updateData["quantity"] = cartModel.quantity
                            updateData["totalPrice"] = cartModel.quantity * cartModel.price!!.toFloat()

                            userCart.child(productsModel.key!!)
                                    .updateChildren(updateData)
                                    .addOnSuccessListener {
                                        EventBus.getDefault().postSticky(UpdateCartEvent())
                                        cartListener.onLoadCartFailed("Successfully added to cart")
                                    }
                                    .addOnFailureListener {
                                        e->  cartListener.onLoadCartFailed(e.message)
                                    }
                        } else { //new item to cart
                            val cartModel = CartModel()
                            cartModel.key = productsModel.key
                            cartModel.name = productsModel.name
                            cartModel.image = productsModel.image
                            cartModel.price = productsModel.price
                            cartModel.quantity = 1
                            cartModel.totalPrice = productsModel.price!!.toFloat()

                            userCart.child(productsModel.key!!)
                                    .setValue(cartModel)
                                    .addOnSuccessListener {
                                        EventBus.getDefault().postSticky(UpdateCartEvent())
                                        cartListener.onLoadCartFailed("Successfully added to cart")
                                    }
                                    .addOnFailureListener {
                                        e->  cartListener.onLoadCartFailed(e.message)
                                    }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cartListener.onLoadCartFailed(error.message)
                    }
                })
    }

}
