package com.example.reciperecommender.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class RecipeFromJson(
        var title: String?,
        val readyInMinutes: Int?,
        val servings: Int?,
        val ingredients: List<String> = listOf<String>(),
        val instructions: String?,
        val image: String?,
        val host: String?,
        val nutrients: Map<String, String> = mapOf<String, String>()
) : Parcelable

//package com.example.reciperecommender.model
//
//
//import android.os.Parcelable
//import kotlinx.parcelize.Parcelize
//
//
//@Parcelize
//data class RecipeFromJson(
//        val title: String?,
//        val total_time: Int?,
//        val yields: String?,
//        val ingredients: List<String> = listOf<String>(),
//        val instructions: String?,
//        val image: String?,
//        val host: String?,
//        val nutrients: Map<String, String> = mapOf<String, String>()
//) : Parcelable