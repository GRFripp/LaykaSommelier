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
import com.example.laykasommelier.data.local.pojo.DrinkReviewItem
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip

class DrinkReviewAdapter(
    private val onSourceClick: (String?) -> Unit  // URL или null
) : ListAdapter<DrinkReviewItem, DrinkReviewAdapter.ReviewViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<DrinkReviewItem>() {
        override fun areItemsTheSame(oldItem: DrinkReviewItem, newItem: DrinkReviewItem) =
            oldItem.reviewId == newItem.reviewId
        override fun areContentsTheSame(oldItem: DrinkReviewItem, newItem: DrinkReviewItem) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drink_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position), onSourceClick)
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSourceName: TextView = itemView.findViewById(R.id.tvSourceName)
        private val tvSourceLink: TextView = itemView.findViewById(R.id.tvSourceLink)
        private val flexboxDescriptors: FlexboxLayout = itemView.findViewById(R.id.flexboxDescriptors)

        fun bind(item: DrinkReviewItem, onSourceClick: (String?) -> Unit) {
            tvSourceName.text = item.sourceName

            // Показ или скрытие ссылки
            if (!item.sourceUrl.isNullOrBlank()) {
                tvSourceLink.visibility = View.VISIBLE
                tvSourceLink.setOnClickListener { onSourceClick(item.sourceUrl) }
            } else {
                tvSourceLink.visibility = View.GONE
                tvSourceLink.setOnClickListener(null) // убираем старый слушатель
            }

            // Чипы дескрипторов
            flexboxDescriptors.removeAllViews()
            item.descriptors.forEach { desc ->
                val chip = Chip(
                    flexboxDescriptors.context,
                    null,
                    com.google.android.material.R.style.Widget_Material3_Chip_Assist
                ).apply {
                    text = desc.name
                    chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(desc.color))
                    setTextColor(Color.WHITE)
                    isClickable = false
                    isCheckable = false
                }
                flexboxDescriptors.addView(chip)
            }
        }
    }
}