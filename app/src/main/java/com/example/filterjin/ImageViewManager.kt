package com.example.filterjin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

class ImageViewManager (context : Context){

    private var imageView : ImageView = ImageView(context)
    private var defaultImage : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.test)
    private var currentImage : Bitmap = defaultImage

    fun getImageView(): ImageView {
        imageView.apply {
            setImageBitmap(currentImage)
            layoutParams = ConstraintLayout.LayoutParams(
                1000,
                1000
            )
            id = ConstraintLayout.generateViewId()
        }
        return imageView
    }


    fun setCurrentImage (bitmap : Bitmap) {
        currentImage = bitmap
        imageView.setImageBitmap(currentImage)
    }

    fun applyFilter(item: FilterItem) {

        if(item.name=="GrayScale"){
            val  grayscaleBitmap = applyGrayScaleFilter(currentImage, item.rRatio,item.bRatio, item.gRatio)
            setCurrentImage(grayscaleBitmap)
        }

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