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
        var id : Int = 0
        var name : String = ""
        var rRatio : Double = 0.0
        var gRatio : Double = 0.0
        var bRatio : Double = 0.0
        var category : String = ""
        var type : String = ""
        var lut : String = ""


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

            var thumbnail : Bitmap = BitmapFactory.decodeStream(inputStream)



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