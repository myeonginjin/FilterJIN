package com.example.filterjin

import android.graphics.Bitmap
import android.media.Image
import android.widget.ImageView

data class FilterItem ( val id : Int, val name : String, var thumbnail : Bitmap , val  rRatio : Double, val  gRatio : Double, val  bRatio : Double, val imagePath : String){
    fun updateThumbnail(newImage : Bitmap){
        thumbnail = newImage
    }
}