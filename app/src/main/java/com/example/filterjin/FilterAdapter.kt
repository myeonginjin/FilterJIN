package com.example.filterjin

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilterAdapter(private var filterItemList: List<FilterItem>):
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

        // 선택 상태에 따른 ImageView 투명도 조정
        holder.itemImg.alpha = if (filterItem.isSelected) 0.2f else 1.0f


        val frameLayout = holder.itemView.findViewById<FrameLayout>(R.id.frameLayoutId)

        if (filterItem.isSelected) {
            // 선택된 경우 테두리 추가
            val borderSize = 10 // 테두리 크기
            val borderColor = Color.WHITE // 테두리 색상
            val shapeDrawable = ShapeDrawable(RectShape()).apply {
                paint.color = borderColor
                paint.strokeWidth = borderSize.toFloat()
                paint.style = Paint.Style.STROKE
            }
            frameLayout.background = shapeDrawable
        } else {
            // 선택되지 않은 경우 테두리 제거
            frameLayout.background = null
        }



        // 선택 상태에 따른 배경색 변경
//        holder.itemView.isSelected = filterItem.isSelected

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
