package com.example.laykasommelier

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.DrinkTypeAdapter
import com.example.laykasommelier.data.local.pojo.DrinkListPreviews
import com.example.laykasommelier.data.local.pojo.DrinkListTypes
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import org.w3c.dom.Text

class DrinkListSelectedAdapter(private val onItemClick: (Long) -> Unit)
    :ListAdapter<DrinkListPreviews, DrinkListSelectedAdapter.ViewHolder>(DiffCallback)
{
    companion object DiffCallback: DiffUtil.ItemCallback<DrinkListPreviews>(){
        override fun areContentsTheSame(oldItem: DrinkListPreviews, newItem: DrinkListPreviews): Boolean{
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: DrinkListPreviews, newItem: DrinkListPreviews): Boolean {
            return oldItem.drinkId == newItem.drinkId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkListSelectedAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.drink_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrinkListSelectedAdapter.ViewHolder, position: Int) {

        holder.bind(getItem(position), onItemClick)

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val drinkImageView: ImageView = itemView.findViewById<ImageView>(R.id.imageView2)
        val drinkTextView: TextView = itemView.findViewById<TextView>(R.id.textView2)
        val drinkDesCategories: FlexboxLayout = itemView.findViewById<FlexboxLayout>(R.id.flexboxCategories)

        fun bind(item: DrinkListPreviews, onItemClick: (Long) -> Unit){

            drinkTextView.text  = item.drinkName

            drinkDesCategories.removeAllViews()
            item.categories.forEach { categoryColor ->
                val chip = Chip(
                    drinkDesCategories.context,
                    null,
                    com.google.android.material.R.style.Widget_Material3_Chip_Assist
                )
                chip.text = if (categoryColor.count > 0) {
                   "${categoryColor.name} (${categoryColor.count})"
                } else {
                    categoryColor.name
                }
                try {
                    val color = Color.parseColor(categoryColor.color)
                    chip.chipBackgroundColor = ColorStateList.valueOf(color)
                    chip.setTextColor(ColorStateList.valueOf(Color.WHITE))
                } catch (e: IllegalArgumentException){
                    chip.chipBackgroundColor = ColorStateList.valueOf(Color.GRAY)
                }
                chip.isClickable = false
                chip.isCheckable = false
                chip.textSize = 12f
                drinkDesCategories.addView(chip)

            }

            //Потом поменяем на Glide, когда на сервере будут данные
            drinkImageView.setImageResource(R.drawable.ic_launcher_background)

            itemView.setOnClickListener {
                onItemClick(item.drinkId)
            }
        }

    }
}