package com.example.laykasommelier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.data.local.pojo.CocktailIngredientLinkItem

class CocktailIngredientLinkAdapter(
    private val onDeleteClick: (Long) -> Unit
) : ListAdapter<CocktailIngredientLinkItem, CocktailIngredientLinkAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<CocktailIngredientLinkItem>() {
        override fun areItemsTheSame(old: CocktailIngredientLinkItem, new: CocktailIngredientLinkItem) =
            old.ingredientId == new.ingredientId
        override fun areContentsTheSame(old: CocktailIngredientLinkItem, new: CocktailIngredientLinkItem) =
            old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cocktail_ingredient_link, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onDeleteClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvIngredientName)
        private val tvVolume: TextView = itemView.findViewById(R.id.tvIngredientVolume)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteIngredient)

        fun bind(item: CocktailIngredientLinkItem, onDelete: (Long) -> Unit) {
            tvName.text = item.ingredientName
            tvVolume.text = "${item.volume} мл"
            btnDelete.setOnClickListener { onDelete(item.ingredientId) }
        }
    }
}