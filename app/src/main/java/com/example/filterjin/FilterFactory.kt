package com.example.filterjin

import android.content.Context
import android.media.Image
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

        for (i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)

            val id = jsonObject.getInt("id")
            val name = jsonObject.getString("name")
            val rRatio = jsonObject.getDouble ("rRatio")
            val gRatio = jsonObject.getDouble ("gRatio")
            val bRatio = jsonObject.getDouble ("bRatio")

            val thumbnail  = R.drawable.grayscale_test
            val imagePath = jsonObject.getString("imagePath")

            val filter = FilterItem(id,name, thumbnail ,rRatio,gRatio , bRatio, imagePath)

            filterList.add(filter)
        }

        return  filterList
    }





}