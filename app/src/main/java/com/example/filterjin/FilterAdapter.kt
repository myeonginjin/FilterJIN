package com.example.filterjin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilterAdapter (val filterItemList : ArrayList<FilterItem>):
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }
    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_item_recycler_view, parent, false)
        return FilterViewHolder(view)
    }
    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.tv_time.text = filterItemList[position].id.toString()
        holder.tv_title.text = filterItemList[position].name
        holder.tv_name.text = filterItemList[position].imagePath
    }

    override fun getItemCount(): Int {
        return filterItemList.count()
    }


    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_time = itemView.findViewById<TextView>(R.id.tv_time)
        val tv_title = itemView.findViewById<TextView>(R.id.tv_title)
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)

        init {
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)


            }


        }

    }
}