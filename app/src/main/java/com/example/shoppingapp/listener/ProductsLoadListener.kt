package com.example.shoppingapp.listener

import com.example.shoppingapp.model.ProductsModel

interface ProductsLoadListener {
    fun onProductsLoadSuccess(productsLoadListener:List<ProductsModel>?)
    fun onProductsLoadFailed(message: String?)
}