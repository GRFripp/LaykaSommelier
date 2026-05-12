package com.example.laykasommelier

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.data.local.pojo.CocktailIngredientItem
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip

class CocktailIngredientAdapter(
    private val onItemClick: (Long) -> Unit = {}
) : ListAdapter<CocktailIngredientItem, CocktailIngredientAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<CocktailIngredientItem>() {
        override fun areItemsTheSame(old: CocktailIngredientItem, new: CocktailIngredientItem) =
            old.ingredientId == new.ingredientId
        override fun areContentsTheSame(old: CocktailIngredientItem, new: CocktailIngredientItem) =
            old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cocktail_ingredient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvIngredientName)
        private val tvAbv: TextView = itemView.findViewById(R.id.tvIngredientAbv)
        private val tvAcidity: TextView = itemView.findViewById(R.id.tvIngredientAcidity)
        private val tvSugar: TextView = itemView.findViewById(R.id.tvIngredientSugar)
        private val tvVolume: TextView = itemView.findViewById(R.id.tvIngredientVolume)
        private val flexboxDescriptors: FlexboxLayout = itemView.findViewById(R.id.flexboxDescriptors)

        fun bind(item: CocktailIngredientItem, onClick: (Long) -> Unit) {
            tvName.text = item.ingredientName
            tvAbv.text = "${item.ingredientAbv}%"
            tvAcidity.text = "pH ${item.ingredientAcidity}"
            tvSugar.text = "${item.ingredientSugarLevel} г/100мл"
            tvVolume.text = "${item.volumeInCocktail} мл"

            flexboxDescriptors.removeAllViews()
            item.descriptors.forEach { desc ->
                val chip = Chip(flexboxDescriptors.context).apply {
                    text = desc.name
                    chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(desc.color))
                    setTextColor(Color.WHITE)
                    isClickable = false
                    isCheckable = false
                }
                flexboxDescriptors.addView(chip)
            }

            itemView.setOnClickListener { onClick(item.ingredientId) }
        }
    }
}