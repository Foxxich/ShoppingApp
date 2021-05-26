package com.example.shoppingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.model.ProductsModel
import com.example.shoppingapp.listener.ItemListener
import java.lang.StringBuilder

class ProductsAdapter(private val context: Context, private val list: List<ProductsModel>, private val itemListener: ItemListener, private val userName: String) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

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
}
