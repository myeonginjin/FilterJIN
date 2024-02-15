package com.example.filterjin

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewManager (private val context : Context,  private val mainLayout: MainLayout , private val imageViewManager : ImageViewManager) {
    private  val editBar = RecyclerView(context)

    private val filterFactory = FilterFactory(context)

    //리싸이클러뷰의 item으로 사용할 data class 데이터타입의 Filter 인스턴스 리스트 받아오기
    private val itemList =  filterFactory.getFilterItemList()

    //어뎁터에 인스턴스 리스트 보내, 뷰 요소로 사용할 수 있도록 만들기
    private val filterAdapter = FilterAdapter(itemList)

    fun getUniqueCategories(): List<String> {
        return filterFactory.getFilterCategoryList().map { it.category }.distinct()
    }




    fun scrollToCategory(category: String) {
        if (category == "ALL") {
            // ALL 카테고리 선택 시, 리스트의 가장 처음으로 스크롤
            (editBar.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(0, 0)
        } else {
            // 다른 카테고리 선택 시, 해당 카테고리의 첫 번째 아이템으로 스크롤
            val position = itemList.indexOfFirst { it.category == category }
            if (position != -1) {
                (editBar.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, 0)
            }
        }
    }

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
//                Toast.makeText(context, "${item.name} 클릭함", Toast.LENGTH_SHORT).show()

                // 메소드 실행 전 시간 측정
                val startTime = System.nanoTime()

                // 메소드 실행
                imageViewManager.applyFilter(item)

                // 메소드 실행 후 시간 측정
                val endTime = System.nanoTime()

                // 실행 시간 계산 (나노초 단위)
                val duration = (endTime - startTime) / 1_000_000_000.0 // 초 단위로 변환

                // 로그로 실행 시간 출력
                Log.i("Performance", "${item.name} 필터 적용 시간: $duration ms")
            }
        }
        return  editBar
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentItemImage(bitmap: Bitmap) {
        // Coroutine 시작
        CoroutineScope(Dispatchers.IO).launch {
            itemList.forEach { item ->
                when (item.type) {
                    "Ratio" -> {
                        item.updateThumbnail(ImageProcessor.applyRatioFilter(bitmap, item.rRatio, item.gRatio, item.bRatio))
                    }
                    "LUT" -> {
                        lateinit var lutBitmap: Bitmap
                        val assetManager = context.resources.assets
                        val fileName: String = item.lut

                        val inputStreamLUT = assetManager.open(fileName)
                        lutBitmap = BitmapFactory.decodeStream(inputStreamLUT)

                        item.updateThumbnail(ImageProcessor.applyLutToBitmap(bitmap, lutBitmap))
                    }
                    else -> {
                        item.updateThumbnail(bitmap)
                    }
                }
            }

            // 메인 스레드로 전환하여 UI 업데이트
            withContext(Dispatchers.Main) {
                // 어댑터와 리사이클러뷰 갱신
                filterAdapter.notifyDataSetChanged()
            }
        }
    }
}