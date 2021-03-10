package com.example.reciperecommender.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reciperecommender.R
import com.example.reciperecommender.RecipeActivity
import com.example.reciperecommender.model.RecipeFromJson
import com.squareup.picasso.Picasso

class ItemAdapter(
        private val context: Context,
        private var dataset: List<RecipeFromJson>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_title)
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val caloriesView: TextView = view.findViewById(R.id.item_calories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount() = dataset.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.textView.text = item.title
        holder.caloriesView.text = item.nutrients["calories"] + " calories"
        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeActivity::class.java)
            intent.putExtra("item", item)
            context.startActivity(intent)
        }

        //Picasso.get().setLoggingEnabled(true)
        Picasso.get().load(item.image).into(holder.imageView)
    }

    fun update(newData: List<RecipeFromJson>?){
        if (newData.isNullOrEmpty())
            dataset = emptyList()
        else
            dataset = newData
        this.notifyDataSetChanged()
    }

}
