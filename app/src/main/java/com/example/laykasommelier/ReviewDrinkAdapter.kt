package com.example.laykasommelier

import androidx.recyclerview.widget.ListAdapter
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.R
import com.example.laykasommelier.data.local.pojo.DrinkReviewItem
import com.example.laykasommelier.data.local.pojo.DescriptorChip
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
class ReviewDrinkAdapter(
    private val onItemClick: (Long) -> Unit
) : ListAdapter<DrinkReviewItem, ReviewDrinkAdapter.ReviewViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<DrinkReviewItem>() {
        override fun areItemsTheSame(old: DrinkReviewItem, new: DrinkReviewItem) =
            old.reviewId == new.reviewId
        override fun areContentsTheSame(old: DrinkReviewItem, new: DrinkReviewItem) =
            old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review_in_drink, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSourceName: TextView = itemView.findViewById(R.id.tvSourceName)
        private val tvSourceLink: TextView = itemView.findViewById(R.id.tvSourceLink)
        private val flexboxDescriptors: FlexboxLayout = itemView.findViewById(R.id.flexboxDescriptors)

        fun bind(item: DrinkReviewItem, onItemClick: (Long) -> Unit) {
            tvSourceName.text = item.sourceName

            // Ссылка на источник – открывается в браузере, если URL присутствует
            if (!item.sourceUrl.isNullOrBlank()) {
                tvSourceLink.visibility = View.VISIBLE
                tvSourceLink.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.sourceUrl))
                    itemView.context.startActivity(intent)
                }
            } else {
                tvSourceLink.visibility = View.GONE
            }

            // Чипы дескрипторов
            flexboxDescriptors.removeAllViews()
            item.descriptors.forEach { desc ->
                val chip = Chip(flexboxDescriptors.context, null, com.google.android.material.R.style.Widget_Material3_Chip_Assist).apply {
                    text = desc.name
                    chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(desc.color))
                    setTextColor(Color.WHITE)
                    isClickable = false
                    isCheckable = false
                }
                flexboxDescriptors.addView(chip)
            }

            // Вся карточка открывает диалог редактирования рецензии
            itemView.setOnClickListener {
                onItemClick(item.reviewId)
            }
        }
    }
}