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
import com.bumptech.glide.Glide
import com.example.laykasommelier.data.local.pojo.SuggestionPreviewItem
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip

class SuggestionListAdapter(
    private val onItemClick: (Long) -> Unit  // передаём suggestionId
) : ListAdapter<SuggestionPreviewItem, SuggestionListAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<SuggestionPreviewItem>() {
        override fun areItemsTheSame(old: SuggestionPreviewItem, new: SuggestionPreviewItem) =
            old.suggestionId == new.suggestionId
        override fun areContentsTheSame(old: SuggestionPreviewItem, new: SuggestionPreviewItem) =
            old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.suggestion_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivPhoto: ImageView = itemView.findViewById(R.id.ivSuggestionPhoto)
        private val tvName: TextView = itemView.findViewById(R.id.tvSuggestionName)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tvSuggestionAuthor)
        private val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)
        private val flexboxDescriptors: FlexboxLayout = itemView.findViewById(R.id.flexboxSuggestionDescriptors)

        fun bind(item: SuggestionPreviewItem, onClick: (Long) -> Unit) {
            tvName.text = item.cocktailName
            tvAuthor.text = "Автор: ${item.employeeName}"

            // Статус в виде чипа
            chipStatus.text = item.status
            chipStatus.chipBackgroundColor = when (item.status.lowercase()) {
                "approved" -> ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                "rejected" -> ColorStateList.valueOf(Color.parseColor("#F44336"))
                else -> ColorStateList.valueOf(Color.parseColor("#FFC107")) // pending
            }

            // Дескрипторы
            flexboxDescriptors.removeAllViews()
            item.descriptors.forEach { desc ->
                val chip = Chip(flexboxDescriptors.context).apply {
                    text = desc.name
                    try {
                        val color = Color.parseColor(desc.color)
                        chipBackgroundColor = ColorStateList.valueOf(color)
                        setTextColor(Color.WHITE)
                    } catch (e: IllegalArgumentException) {
                        chipBackgroundColor = ColorStateList.valueOf(Color.GRAY)
                    }
                    isClickable = false
                    isCheckable = false
                }
                flexboxDescriptors.addView(chip)
            }

            val imageUrl = item.imageUrl
            if (!imageUrl.isNullOrEmpty()) {
                val fullUrl = "http://10.0.2.2:5169" +
                        (if (imageUrl.startsWith("/")) imageUrl else "/$imageUrl")
                Glide.with(itemView.context)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(ivPhoto)
            } else {
                ivPhoto.setImageResource(R.drawable.ic_launcher_background)
            }

            itemView.setOnClickListener { onClick(item.suggestionId) }
        }
    }
}