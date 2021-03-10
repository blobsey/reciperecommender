package com.example.reciperecommender.model;
import android.annotation.SuppressLint
import android.os.Parcelable

import kotlinx.android.parcel.Parcelize


public @SuppressLint("ParcelCreator")
@Parcelize
data class ResponseFromSpoonacular(
    val results: List<Result>?,
    val baseUri: String?,
    val number: Int?,
    val offset: Int?,
    val processingTimeMs: Int?,
    val totalResults: Int?
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Result(
    val aggregateLikes: Int?,
    val analyzedInstructions: List<AnalyzedInstruction>?,
    val calories: Int?,
    val carbs: String?,
    val cheap: Boolean?,
    val creditsText: String?,
    val cuisines: List<String>?,
    val dairyFree: Boolean?,
    val diets: List<String>?,
    val dishTypes: List<String>?,
    val fat: String?,
    val gaps: String?,
    val glutenFree: Boolean?,
    val healthScore: Double?,
    val id: Int?,
    val image: String?,
    val imageType: String?,
    val license: String?,
    val likes: Int?,
    val lowFodmap: Boolean?,
    val missedIngredientCount: Int?,
    val missedIngredients: List<MissedIngredient>?,
    val occasions: List<String>?,
    val pricePerServing: Double?,
    val protein: String?,
    val readyInMinutes: Int?,
    val servings: Int?,
    val sourceName: String?,
    val sourceUrl: String?,
    val spoonacularScore: Double?,
    val spoonacularSourceUrl: String?,
    val summary: String?,
    val sustainable: Boolean?,
    val title: String?,
    val unusedIngredients: List<UnusedIngredient>?,
    val usedIngredientCount: Int?,
    val usedIngredients: List<UsedIngredient>?,
    val vegan: Boolean?,
    val vegetarian: Boolean?,
    val veryHealthy: Boolean?,
    val veryPopular: Boolean?,
    val weightWatcherSmartPoints: Int?
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class AnalyzedInstruction(
    val name: String?,
    val steps: List<Step>?
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class MissedIngredient(
    val aisle: String?,
    val amount: Double?,
    val id: Int?,
    val image: String?,
    val meta: List<String>?,
    val metaInformation: List<String>?,
    val name: String?,
    val original: String?,
    val originalName: String?,
    val originalString: String?,
    val unit: String?,
    val unitLong: String?,
    val unitShort: String?
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class UnusedIngredient(
    val aisle: String?,
    val amount: Double?,
    val id: Int?,
    val image: String?,
    val meta: List<String>?,
    val metaInformation: List<String>?,
    val name: String?,
    val original: String?,
    val originalName: String?,
    val originalString: String?,
    val unit: String?,
    val unitLong: String?,
    val unitShort: String?
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class UsedIngredient(
    val aisle: String?,
    val amount: Double?,
    val extendedName: String?,
    val id: Int?,
    val image: String?,
    val meta: List<String>?,
    val metaInformation: List<String>?,
    val name: String?,
    val original: String?,
    val originalName: String?,
    val originalString: String?,
    val unit: String?,
    val unitLong: String?,
    val unitShort: String?
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Step(
    val equipment: List<Equipment>?,
    val ingredients: List<Ingredient>?,
    val length: Length?,
    val number: Int?,
    val step: String?
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Equipment(
    val id: Int?,
    val image: String?,
    val localizedName: String?,
    val name: String?
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Ingredient(
    val id: Int?,
    val image: String?,
    val localizedName: String?,
    val name: String?
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Length(
    val number: Int?,
    val unit: String?
) : Parcelable