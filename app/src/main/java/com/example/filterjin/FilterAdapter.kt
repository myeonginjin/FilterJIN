package com.example.filterjin

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilterAdapter(var filterItemList: List<FilterItem>):
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
        holder.itemImg.setImageBitmap(filterItemList[position].thumbnail)
        holder.filterName.text = filterItemList[position].name
    }

    override fun getItemCount(): Int {
        return filterItemList.count()
    }




    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImg = itemView.findViewById<ImageView>(R.id.userImg)
        val filterName = itemView.findViewById<TextView>(R.id.filter_name)

        init {

            itemView.setOnClickListener {

                //인터페이스인 리스너의 콜백 함수 onItemClick(추상함수) 구현
                itemClickListener?.onItemClick(adapterPosition)


            }


        }

    }


}


