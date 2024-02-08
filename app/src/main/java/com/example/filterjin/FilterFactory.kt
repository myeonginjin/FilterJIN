package com.example.filterjin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class FilterFactory (private val context: Context) {
    private val filterList = ArrayList<FilterItem>()
    private var jsonArray = JSONArray()

    init  {
        val jsonString = context.assets.open("filterData.json").reader().readText()
        jsonArray = JSONArray(jsonString)
    }

    fun getFilterItemList() : List<FilterItem>{

        Log.d("FilterFactory", "getFilterItemList 시작")

        for (i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)

            val id = jsonObject.getInt("id")
            val name = jsonObject.getString("name")
            val rRatio = jsonObject.getDouble ("rRatio")
            val gRatio = jsonObject.getDouble ("gRatio")
            val bRatio = jsonObject.getDouble ("bRatio")

            val defaultImage  = BitmapFactory.decodeResource(context.resources, R.drawable.default_image)

            var thumbnail : Bitmap

            if (name == "GrayScale"){
                thumbnail = ImageProcessor.applyGrayScaleFilter(defaultImage, rRatio, gRatio, bRatio)
            }
            else if(rRatio == 0.0 && bRatio == 0.0 && gRatio == 0.0) {
                lateinit var lutBitmap: Bitmap
                val assetManager = context.resources.assets

                if(true){
                    val inputStreamLUT = assetManager.open("grayscale.jpeg")
                    lutBitmap = BitmapFactory.decodeStream(inputStreamLUT)
                }
                val applyLutBitmap = ImageProcessor.applyLutToBitmap(defaultImage , lutBitmap)
                thumbnail = applyLutBitmap
            }

            else{
                thumbnail = defaultImage
            }

            val imagePath = jsonObject.getString("imagePath")

            try {

                val filter = FilterItem(id, name, thumbnail, rRatio, gRatio, bRatio, imagePath)
                filterList.add(filter)

            }catch (e : Exception){
                Log.e("getFilterItemList_error","${e.message}" )
            }
        }

        return  filterList
    }
}