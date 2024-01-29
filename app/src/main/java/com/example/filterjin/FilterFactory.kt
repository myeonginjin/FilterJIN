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
            val thumbnail  = BitmapFactory.decodeResource(context.resources, R.drawable.test)
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