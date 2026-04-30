package com.example.laykasommelier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.data.local.pojo.DrinkListTypes
import kotlinx.coroutines.flow.Flow

class DrinkTypeAdapter(
    private val onItemClick: (String) -> Unit
): ListAdapter<DrinkListTypes, DrinkTypeAdapter.ViewHolder>(DiffCallback)
{
    companion object DiffCallback: DiffUtil.ItemCallback<DrinkListTypes>(){
        override fun areContentsTheSame(oldItem: DrinkListTypes, newItem: DrinkListTypes): Boolean{
            return oldItem.drinkListTypeCount == newItem.drinkListTypeCount &&
                    oldItem.drinkListType == newItem.drinkListType
        }

        override fun areItemsTheSame(oldItem: DrinkListTypes, newItem: DrinkListTypes): Boolean {
            return oldItem.drinkListType == newItem.drinkListType
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.drink_type_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position), onItemClick)

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val dtNametv: TextView = itemView.findViewById(R.id.dtNametv)
        private val dtNCounttv: TextView = itemView.findViewById(R.id.dtCounttv)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(item: DrinkListTypes, onItemClick: (String) -> Unit){
            dtNametv.text  = item.drinkListType
            dtNCounttv.text = item.drinkListTypeCount.toString()
            imageView.setImageResource(getIconForType(item.drinkListType))

            itemView.setOnClickListener {
                onItemClick(item.drinkListType)
            }
        }
        fun getIconForType(type: String): Int = when (type) {
            "Виски" -> R.drawable.whiskeyimage
            "Ром"   -> R.drawable.rumimage
            "Вино" -> R.drawable.wineimage
            else    -> R.drawable.ic_launcher_background
        }
    }

}