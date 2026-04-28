package com.example.laykasommelier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.data.local.pojo.DrinkListTypes
import kotlinx.coroutines.flow.Flow

class DrinkTypeAdapter(
    private var items: List<DrinkListTypes>,
    private val onItemClick: (String) -> Unit
): RecyclerView.Adapter<DrinkTypeAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.drink_type_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

    }
    fun updateItems(newItems: List<DrinkListTypes>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val dtNametv: TextView = itemView.findViewById(R.id.dtNametv)
        private val dtNCounttv: TextView = itemView.findViewById(R.id.dtCounttv)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(item: DrinkListTypes){
            dtNametv.text  = item.drinkListType
            dtNCounttv.text = item.drinkListTypeCount.toString()
            imageView.setImageResource(getIconForType(item.drinkListType))
        }
        fun getIconForType(type: String): Int = when (type) {
            "Виски" -> R.drawable.whiskeyimage
            "Ром"   -> R.drawable.rumimage
            "Вино" -> R.drawable.wineimage
            else    -> R.drawable.ic_launcher_background
        }
    }

}