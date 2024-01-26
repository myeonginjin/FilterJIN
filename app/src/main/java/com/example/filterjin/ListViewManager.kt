package com.example.filterjin

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

    fun getEditBar() : RecyclerView{

        editBar.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        //리싸이클러뷰의 item으로 사용할 data class 데이터타입의 Filter 인스턴스 리스트 받아오기
        val itemList =  FilterFactory(context).getFilterItemList()

        //어뎁터에 인스턴스 리스트 보내, 뷰 요소로 사용할 수 있도록 만들기
        val filterAdapter = FilterAdapter(itemList)

        //어뎁터와 리싸이클러 뷰 갱신
        filterAdapter.notifyDataSetChanged()

        editBar.adapter = filterAdapter

        //리스트가 좌우로 스크롤되도록 지정
        editBar.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        filterAdapter.itemClickListener = object : FilterAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val item = itemList[position]
                Toast.makeText(context, "${item.name} 클릭함", Toast.LENGTH_SHORT).show()

                if (item.name=="GrayScale"){
                    val bitmapImg = imageViewManager.getCurrentImage()

                    val newBitmapImg =
                        applyGrayScaleFilter(bitmapImg, item.rRatio,item.bRatio, item.gRatio )

                    imageViewManager.setCurrentImage(newBitmapImg)

                }
            }
        }
        return  editBar

    }

    private fun applyGrayScaleFilter(originalBitmap : Bitmap, rRatio:Double, gRatio:Double, bRatio:Double) : Bitmap{
        // Get the width and height of the bitmap
        val width: Int = originalBitmap.width
        val height: Int = originalBitmap.height

        Log.d("Filter", "Applying grayscale filter")

        Log.i("size333","w: $width    h : $height")
        // Get the pixels of the bitmap
        val pixels: IntArray = IntArray(width * height)
        originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // Constants for grayscale conversion
//        val rRatio = 0.299
//        val gRatio = 0.587
//        val bRatio = 0.114

        // Apply grayscale filter
        for (i in 0 until width * height) {
            val pixel = pixels[i]

            // Extract RGB values
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)

            // Calculate grayscale value
            val gray1 = (red * rRatio + green * gRatio + blue * bRatio).toInt()

            // Create new RGB value (all color channels have the same value)
            pixels[i] = Color.rgb(gray1, gray1, gray1)
        }

        // Create a new bitmap with the modified pixels
        val grayscaleBitmap =
            Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)

        return grayscaleBitmap

    }
}