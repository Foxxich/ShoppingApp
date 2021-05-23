package com.example.shoppingapp.listener

import android.os.Message
import com.example.cartapp.model.ProductsModel

interface ProductsLoadListener {
    fun onProductsLoadSuccess(productsLoadListener:List<ProductsModel>?)
    fun onProductsLoadFailed(message: String?)
}