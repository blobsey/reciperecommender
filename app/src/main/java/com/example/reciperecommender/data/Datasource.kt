package com.example.reciperecommender.data

import android.content.Context
import android.util.Log
import com.example.reciperecommender.R
import com.example.reciperecommender.model.RecipeFromJson
import com.example.reciperecommender.model.ResponseFromSpoonacular
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import java.util.concurrent.CountDownLatch

class Datasource {
    fun loadRecipes(
        applicationContext: Context,
        ingredients: List<String>,
        offset: Int,
        number: Int,
        calories: Int?
    ): List<RecipeFromJson>
    {
        val response = apiCall(applicationContext, ingredients, offset, number, calories)
        val list = mutableListOf<RecipeFromJson>()

        if (response == null)
            return emptyList()

        for (recipe in response.results!!)
        {
            val recipeIngredients = mutableListOf<String>()
            for (ingredient in recipe.usedIngredients!!)
                ingredient.name?.let { recipeIngredients.add(it) }
            for (ingredient in recipe.missedIngredients!!)
                ingredient.name?.let { recipeIngredients.add(it) }

            val recipeInstructions = StringBuilder()
            for (instruction in recipe.analyzedInstructions!!)
                for (step in instruction.steps!!)
                    recipeInstructions.append("-" + step.step + "\n")

            list.add(
                RecipeFromJson(
                    recipe.title,
                    recipe.readyInMinutes,
                    recipe.servings,
                    recipeIngredients,
                    recipeInstructions.toString(),
                    recipe.image,
                    recipe.sourceUrl,
                    mapOf(
                        "calories" to recipe.calories!!.toString(),
                        "proteinContent" to recipe.protein!!,
                        "fatContent" to recipe.fat!!,
                        "carbohydrateContent" to recipe.carbs!!
                    )
                )
            )
        }

        return list
    }

    private fun apiCall(
        applicationContext: Context,
        ingredients: List<String>,
        offset: Int,
        number: Int,
        calories: Int?
    ): ResponseFromSpoonacular? {
        val client = OkHttpClient()
        val gson = Gson()

        var resp : ResponseFromSpoonacular? = null
        val url = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/searchComplex" +
                    "?limitLicense=false" +
                    "&offset=$offset" +
                    "&number=$number" +
                    "&query=${ingredients.lastOrNull()?:"healthy"}" +
                    "&includeIngredients=${ingredients.joinToString(separator = "%2C%20")}" +
                    "&type=main%20course" +
                    "&ranking=1" +
                    "&minCalories=${calories?.minus((calories*.70).toInt()) ?:150}" +
                    "&maxCalories=${calories?.plus((calories*.30).toInt()) ?:1000}" +
                    "&fillIngredients=true" +
                    "&instructionsRequired=true" +
                    "&addRecipeInformation=true"

        Log.i("APICALL", url)


        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("x-rapidapi-key", applicationContext.getString(R.string.rapidapi_key))
            .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
            .build()

            val countDownLatch = CountDownLatch(1)

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    countDownLatch.countDown()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            countDownLatch.countDown()
                            throw IOException("Unexpected code $response")
                        }

                        val responseType = object : TypeToken<ResponseFromSpoonacular>() {}.type
                        resp = gson.fromJson(response.body!!.string(), responseType)
                        countDownLatch.countDown()
                    }
                }
            })
        countDownLatch.await();
        return resp
    }


}