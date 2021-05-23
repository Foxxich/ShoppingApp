package com.example.shoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingapp.listener.ProductsLoadListener
import com.example.cartapp.model.ProductsModel
import com.example.shoppingapp.details.ProductDetailsActivity
import com.example.shoppingapp.eventbus.UpdateCartEvent
import com.example.shoppingapp.listener.ICartLoadListener
import com.example.shoppingapp.listener.ItemListener
import com.example.shoppingapp.menu_activities.MapsActivity
import com.example.shoppingapp.model.CartModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), ProductsLoadListener, ICartLoadListener,
    ItemListener {

    lateinit var productsLoadListener: ProductsLoadListener
    lateinit var iCartLoadListener: ICartLoadListener
    private var adapter: ProductsAdapter? = null

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java)) {
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        }
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public fun onUpdateCrtEvent(event:UpdateCartEvent) {
        countCartFRomFirebase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        loadProductsFromFirebase()
        countCartFRomFirebase()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i("UI_INFO", "Selected Item: " + item.title)
        return when (item.itemId) {
            R.id.maps_item ->
            {
                mapClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mapClicked() {
        val intent = Intent(this, MapsActivity::class.java).apply {
            putExtra("showMap", "" )
        }
        startActivityForResult(intent, 1)
    }

    private fun countCartFRomFirebase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        iCartLoadListener.onLoadCartFailed(error.message)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(cartSnapshot in snapshot.children) {
                            val cartModel = cartSnapshot.getValue(CartModel::class.java)
                            cartModel!!.key = cartSnapshot.key
                            cartModels.add(cartModel)
                        }
                        iCartLoadListener.onLoadCartSuccess(cartModels)
                    }

                })
    }

    private fun loadProductsFromFirebase() {
        val productsModels : MutableList<ProductsModel> = ArrayList()
        FirebaseDatabase.getInstance()
                .getReference("Game")
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        productsLoadListener.onProductsLoadFailed(error.message)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()) {
                            for(productSnapshot in snapshot.children) {
                                val productModel = productSnapshot.getValue(ProductsModel::class.java)
                                productModel!!.key = productSnapshot.key
                                productsModels.add(productModel)
                            }
                            productsLoadListener.onProductsLoadSuccess(productsModels)
                        } else {
                            productsLoadListener.onProductsLoadFailed("Games do not exist")
                        }
                    }
                })
    }

    private fun init() {
        productsLoadListener = this
        productsLoadListener = this
        iCartLoadListener = this

        val gridLayoutManager = GridLayoutManager(this, 2)
        recycler_drink.layoutManager = gridLayoutManager
        recycler_drink.addItemDecoration(SpaceItemDecoration())
    }

    override fun onProductsLoadSuccess(productsModelList: List<ProductsModel>?) {
        adapter = ProductsAdapter(this,productsModelList!!, iCartLoadListener,this )
        recycler_drink.adapter = adapter
    }

    override fun onProductsLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var cartSum = 0
        for(cartModel in cartModelList) cartSum += cartModel.quantity
        badge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun clickedLong(productsModel: Int) {
        val intent = Intent(this, ProductDetailsActivity::class.java).apply {
            putExtra("itemToShow", productsModel.toString())
        }
        startActivityForResult(intent, 2)
    }
}