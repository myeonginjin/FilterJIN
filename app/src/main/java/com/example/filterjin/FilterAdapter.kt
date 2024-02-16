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
        fun onItemClick(position: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_item_recycler_view, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filterItem = filterItemList[position]

        holder.itemImg.setImageBitmap(filterItem.thumbnail)
        holder.filterName.text = filterItem.name

        // 선택 상태에 따른 배경색 변경
        holder.itemView.isSelected = filterItem.isSelected

        holder.itemView.setOnClickListener {
            val previousIndex = filterItemList.indexOfFirst { it.isSelected }
            if (previousIndex != -1 && previousIndex != position) {
                filterItemList[previousIndex].isSelected = false
                notifyItemChanged(previousIndex)
            }

            // 현재 아이템의 선택 상태 반전
            filterItem.isSelected = !filterItem.isSelected
            notifyItemChanged(position)

            // 콜백 호출
            itemClickListener?.onItemClick(position)
        }
    }

    fun resetFilterSelection() {
        filterItemList.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filterItemList.count()
    }

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImg: ImageView = itemView.findViewById(R.id.userImg)
        val filterName: TextView = itemView.findViewById(R.id.filter_name)
    }
}
