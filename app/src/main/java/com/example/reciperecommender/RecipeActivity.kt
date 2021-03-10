package com.example.reciperecommender

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.reciperecommender.model.RecipeFromJson
import com.squareup.picasso.Picasso


class RecipeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val recipe = intent.getParcelableExtra<RecipeFromJson>("item")
        val ingredientsBuilder = StringBuilder()
        val stepsBuilder = StringBuilder()

        if (recipe != null) {
            findViewById<TextView>(R.id.recipeTitle).text = recipe.title
            Picasso.get().load(recipe.image).into(findViewById<ImageView>(R.id.recipeImage))
            for (ingredient in recipe.ingredients) {
                ingredientsBuilder.append("$ingredient\n")
            }
            findViewById<TextView>(R.id.recipeCalories).text = "Calories per serving: ${recipe.nutrients["calories"]}"

            findViewById<TextView>(R.id.recipeIngredients).text = ingredientsBuilder.toString()

            findViewById<TextView>(R.id.recipeInstructions).text = recipe.instructions

        }
    }
}