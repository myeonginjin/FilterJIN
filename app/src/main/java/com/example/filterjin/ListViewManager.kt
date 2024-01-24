package com.example.filterjin

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListViewManager (private val context : Context) {
    private  val editBar = RecyclerView(context)

    fun getEditBar() : RecyclerView{

        editBar.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }


        val itemList = ArrayList<FilterItem>()

        itemList.add(FilterItem(1,"GrayScale","LUT.1"))
        itemList.add(FilterItem(2,"Lovely","LUT.2"))
        itemList.add(FilterItem(3,"Tokyo","LUT.3"))
        itemList.add(FilterItem(4,"Pari","LUT.4"))

        val filterAdapter = FilterAdapter(itemList)
        filterAdapter.notifyDataSetChanged()

        editBar.adapter = filterAdapter
        editBar.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        filterAdapter.itemClickListener = object : FilterAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val item = itemList[position]
                Toast.makeText(context, "${item.name} 클릭함", Toast.LENGTH_SHORT).show()
            }
        }
        return  editBar

    }
}