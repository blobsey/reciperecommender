package com.example.reciperecommender

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.reciperecommender.adapter.ItemAdapter
import com.example.reciperecommender.data.Datasource
import com.example.reciperecommender.model.RecipeFromJson
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field.FIELD_CALORIES
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    val mealNames = listOf("Breakfast", "Lunch", "Dinner")
    val caloriePortion = listOf(.22, .44, .44)

    var caloriesWeek = 0.0
    var calorieGoal = 0
    var timeOfDay = 0
    var myDataset : List<RecipeFromJson> = emptyList()
    //var googleId: String? = ""


    lateinit var prefs : SharedPreferences
    val ingredientsPref = "com.example.reciperecommender.ingredients"
    lateinit var myIngredients : MutableSet<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        prefs = getSharedPreferences("com.example.reciperecommender", Context.MODE_PRIVATE)

        if (LocalDateTime.now().hour < 8)
            timeOfDay = 0
        else if (LocalDateTime.now().hour < 17)
            timeOfDay = 1
        else
            timeOfDay = 2

        val countDownLatch = CountDownLatch(1)

        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this, // your activity
                1, // e.g. 1
                account,
                fitnessOptions
            )
        } else {
            accessGoogleFit()
        }
        //countDownLatch.await()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        myIngredients = getIngredients()

//        myDataset = Datasource().loadRecipes(
//            this,
//            myIngredients.toList() as List<String>,
//            0,
//            20,
//            calorieGoal)

        recyclerView.adapter = ItemAdapter(this, myDataset)
        recyclerView.setHasFixedSize(true)

        rankRecipes(recyclerView)


        findViewById<EditText>(R.id.ingredientEntry).setOnEditorActionListener { text, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                myIngredients.remove(text.text.toString())
                myIngredients.add(text.text.toString())
                saveIngredients()

                myDataset = Datasource().loadRecipes(
                    this,
                    myIngredients.toList() as List<String>,
                    0,
                    20,
                    calorieGoal
                )

                rankRecipes(recyclerView)

                true
            }
            false
        }

        //val caloriesView = findViewById<TextView>(R.id.Calories)



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.clearcache, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.clearIngredients -> {
            myIngredients.clear()
            saveIngredients()

            myDataset = emptyList()

            rankRecipes(this.findViewById<RecyclerView>(R.id.recycler_view))
            true
        }


        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .build()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                1 -> accessGoogleFit()
                else -> {
                    // Result wasn't from Google Fit
                }
            }
            else -> {
                // Permission not granted
            }
        }

    }

    private fun saveIngredients() {
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(myIngredients)
        editor.remove(ingredientsPref)
        editor.putString(ingredientsPref, json)
        editor.apply()
    }

    private fun getIngredients(): MutableSet<String?> {
        val gson = Gson()
        val json = prefs.getString(ingredientsPref, null)
        val stringListType = object : TypeToken<MutableSet<String?>>() {}.type
        val list : MutableSet<String?>? = gson.fromJson(json, stringListType)
        if (list.isNullOrEmpty()) {
            return mutableSetOf()
        }
        return list
    }

    private fun rankRecipes(recyclerView: RecyclerView){
        val rankedList : MutableList<Pair<Double, RecipeFromJson>> = mutableListOf()
        for (recipe in myDataset){
            var score = 2000.0  - abs(
                calorieGoal.toDouble() - (recipe.nutrients["calories"]?.toDouble() ?: 2000.0)
            )
            rankedList.add(Pair(score, recipe))
        }
        rankedList.sortByDescending{it.first}
        myDataset = rankedList.map{it.second}
        (recyclerView.adapter as ItemAdapter).update(myDataset)

    }

    @SuppressLint("SetTextI18n")
    private fun accessGoogleFit() {
        val end = LocalDateTime.now().minusDays(1)
        val start = end.minusDays(7)
        val endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond()
        val startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond()

        val readRequest = DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build()
        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        Fitness.getHistoryClient(this, account)
                .readData(readRequest)
                .addOnSuccessListener { response ->
                    // The aggregate query puts datasets into buckets, so flatten into a single list of datasets
                    for (dataSet in response.buckets.flatMap { it.dataSets }) {
                        //dumpDataSet(dataSet)
                        caloriesWeek += dataSet.dataPoints[0].getValue(FIELD_CALORIES).asFloat()

                    }
                    calorieGoal = (caloriesWeek*caloriePortion[timeOfDay]/7).toInt()
                    this.findViewById<TextView>(R.id.Calories).text = "Personalized calorie goal: ${calorieGoal} calories"
                    myDataset = Datasource().loadRecipes(
                        this,
                        myIngredients.toList() as List<String>,
                        0,
                        20,
                        calorieGoal
                    )

                    rankRecipes(findViewById<RecyclerView>(R.id.recycler_view))

                }
                .addOnFailureListener { e -> Log.d("MainActivity", "OnFailure()", e)
                }
    }

    fun dumpDataSet(dataSet: DataSet) {
        Log.i("MainActivity", "Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints) {
            Log.i("MainActivity", "Data point:")
            Log.i("MainActivity", "\tType: ${dp.dataType.name}")
            Log.i("MainActivity", "\tStart: ${dp.getStartTimeString()}")
            Log.i("MainActivity", "\tEnd: ${dp.getEndTimeString()}")
            for (field in dp.dataType.fields) {
                Log.i(
                    "MainActivity",
                    "\tField: ${field.name.toString()} Value: ${dp.getValue(field)}"
                )
            }
        }
    }

    fun DataPoint.getStartTimeString() = Instant.ofEpochSecond(this.getStartTime(TimeUnit.SECONDS))
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime().toString()

    fun DataPoint.getEndTimeString() = Instant.ofEpochSecond(this.getEndTime(TimeUnit.SECONDS))
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime().toString()
}