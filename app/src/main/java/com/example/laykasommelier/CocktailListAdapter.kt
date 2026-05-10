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
import com.example.laykasommelier.data.local.pojo.CocktailListPreviews
import com.example.laykasommelier.data.local.pojo.DrinkListPreviews
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip

class CocktailListAdapter(private val onItemClick: (Long)-> Unit)
    : ListAdapter<CocktailListPreviews, CocktailListAdapter.ViewHolder>(DiffCallback){

    companion object DiffCallback: DiffUtil.ItemCallback<CocktailListPreviews>(){
        override fun areContentsTheSame(oldItem: CocktailListPreviews, newItem: CocktailListPreviews): Boolean{
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: CocktailListPreviews, newItem: CocktailListPreviews): Boolean {
            return oldItem.cocktailId == newItem.cocktailId
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CocktailListAdapter.ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cocktail_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CocktailListAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position),onItemClick)
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nameTv : TextView = itemView.findViewById(R.id.cocktailListName)
        val imageIv : ImageView = itemView.findViewById(R.id.cocktailLIstImage)
        val descriptorsFl : FlexboxLayout = itemView.findViewById(R.id.flexboxCocktailList)

        fun bind(item: CocktailListPreviews, onItemClick: (Long) -> Unit){
            nameTv.text = item.cocktailName
            descriptorsFl.removeAllViews()
            item.descriptors.forEach { descriptors ->
                val chip = Chip(
                    descriptorsFl.context,
                    null,
                    com.google.android.material.R.style.Widget_Material3_Chip_Assist
                )
                chip.text = "${descriptors.name}"
                try {
                    val color = Color.parseColor(descriptors.color)
                    chip.chipBackgroundColor = ColorStateList.valueOf(color)
                    chip.setTextColor(ColorStateList.valueOf(Color.WHITE))
                } catch(e: IllegalArgumentException) {
                    chip.chipBackgroundColor = ColorStateList.valueOf(Color.GRAY)
                }
                chip.isClickable = false
                chip.isCheckable = false
                chip.textSize = 12f
                descriptorsFl.addView(chip)
            }
            itemView.setOnClickListener {
                onItemClick(item.cocktailId)
            }
        }

    }
}
