package com.example.filterjin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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


    fun getFilterCategoryList() :  List<FilterItem>{
        return filterList
    }


    fun getFilterItemList() : List<FilterItem>{

        var  jsonObject :JSONObject
        var id = 0
        var name = ""
        var rRatio = 0.0
        var gRatio = 0.0
        var bRatio = 0.0
        var category = ""
        var type = ""
        var lut = ""


        for (i in 0 until jsonArray.length()){

            try {
                jsonObject = jsonArray.getJSONObject(i)
                id = jsonObject.getInt("id")
                name = jsonObject.getString("name")
                rRatio = jsonObject.getDouble ("rRatio")
                gRatio = jsonObject.getDouble ("gRatio")
                bRatio = jsonObject.getDouble ("bRatio")
                category = jsonObject.getString ("category")
                type = jsonObject.getString ("type")
                lut = jsonObject.getString ("LUT")
            }catch (e :Exception){
                e.printStackTrace()
            }



            val assetManager = context.resources.assets
            val inputStream = assetManager.open("defaultItemThumnailImage.png")

            val thumbnail : Bitmap = BitmapFactory.decodeStream(inputStream)



            try {

                val filter = FilterItem(id, name, thumbnail, rRatio, gRatio, bRatio, category, type, lut)
                filterList.add(filter)

            }catch (e : Exception){
                Log.e("getFilterItemList_error","${e.message}" )
            }
        }

        return  filterList
    }
}