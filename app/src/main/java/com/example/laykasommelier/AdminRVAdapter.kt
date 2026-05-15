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
import com.example.laykasommelier.data.local.pojo.AdminListItem


class AdminRVAdapter(
    private val onItemClick: (AdminListItem) -> Unit
) : ListAdapter<AdminListItem, AdminRVAdapter.UniversalViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<AdminListItem>() {
        override fun areContentsTheSame(oldItem: AdminListItem, newItem: AdminListItem): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: AdminListItem, newItem: AdminListItem): Boolean {

            if (oldItem::class != newItem::class) return false
            return when (oldItem) {
                is AdminListItem.ALDescriptor -> oldItem.id == (newItem as AdminListItem.ALDescriptor).id
                is AdminListItem.ALEmployee -> oldItem.id == (newItem as AdminListItem.ALEmployee).id
                is AdminListItem.ALIngredient -> oldItem.id == (newItem as AdminListItem.ALIngredient).id
                is AdminListItem.ALDescriptorCategory -> oldItem.id == (newItem as AdminListItem.ALDescriptorCategory).id
                is AdminListItem.ALMakingMethod -> oldItem.id == (newItem as AdminListItem.ALMakingMethod).id
                else -> false
            }
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.admin_preview_item


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversalViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.admin_preview_item, parent, false)
        return UniversalViewHolder(view)
    }

    override fun onBindViewHolder(holder: UniversalViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)

    }
    class UniversalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val icon: ImageView = itemView.findViewById(R.id.adminItemIV)
        private val tv1: TextView = itemView.findViewById(R.id.adminItemTV1)
        private val tv2: TextView = itemView.findViewById(R.id.adminItemTV2)

        fun bind(item: AdminListItem, onItemClick: (AdminListItem) -> Unit) {
            itemView.setOnClickListener(null)
            when (item) {
                is AdminListItem.ALDescriptor -> {
                    icon.setImageResource(R.drawable.ic_launcher_background)
                    tv1.text = item.name
                    tv2.text = item.category
                }

                is AdminListItem.ALDescriptorCategory -> {
                    icon.setImageResource(R.drawable.ic_launcher_background)
                    tv1.text = item.name
                    tv2.text = item.color
                }

                is AdminListItem.ALEmployee -> {
                    icon.setImageResource(R.drawable.ic_launcher_background)
                    tv1.text = item.name
                    tv2.text = item.email
                }

                is AdminListItem.ALIngredient -> {
                    icon.setImageResource(R.drawable.ic_launcher_background)
                    tv1.text = item.name
                    tv2.text = item.abv.toString()
                }

                is AdminListItem.ALMakingMethod -> {
                    icon.setImageResource(R.drawable.ic_launcher_background)
                    tv1.text = item.name
                    tv1.text = item.dilution.toString()
                }
            }
            itemView.setOnClickListener { onItemClick(item) }

        }

    }
}