package com.example.filterjin

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListViewManager (private val context : Context,  private val mainLayout: MainLayout , private val imageViewManager : ImageViewManager) {
    private  val editBar = RecyclerView(context)

    //리싸이클러뷰의 item으로 사용할 data class 데이터타입의 Filter 인스턴스 리스트 받아오기
    private val itemList =  FilterFactory(context).getFilterItemList()

    //어뎁터에 인스턴스 리스트 보내, 뷰 요소로 사용할 수 있도록 만들기
    private val filterAdapter = FilterAdapter(itemList)

    @SuppressLint("NotifyDataSetChanged")
    fun getEditBar() : RecyclerView{

        editBar.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }


        //어뎁터와 리싸이클러 뷰 갱신
        filterAdapter.notifyDataSetChanged()

        editBar.adapter = filterAdapter

        //리스트가 좌우로 스크롤되도록 지정
        editBar.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        filterAdapter.itemClickListener = object : FilterAdapter.OnItemClickListener {

            //콜백함수 오바라이딩
            override fun onItemClick(position: Int) {
                val item = itemList[position]
                Toast.makeText(context, "${item.name} 클릭함", Toast.LENGTH_SHORT).show()

                imageViewManager.applyFilter(item)
            }
        }
        return  editBar
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentItemImage (bitmap: Bitmap){
        itemList.forEach { item -> item.updateThumbnail(bitmap)}

        //어뎁터와 리싸이클러 뷰 갱신
        filterAdapter.notifyDataSetChanged()
    }
}