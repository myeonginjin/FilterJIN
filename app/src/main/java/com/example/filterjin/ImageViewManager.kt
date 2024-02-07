package com.example.filterjin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

class ImageViewManager (private val context : Context){

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

    fun getCurrentImage(): Bitmap {

        return currentImage
    }


    fun setCurrentImage (bitmap : Bitmap) {
        currentImage = bitmap
        imageView.setImageBitmap(currentImage)
    }

    fun applyFilter(item: FilterItem) {

        when {
            item.name=="GrayScale" -> {

                val  grayscaleBitmap = ImageProcessor.applyGrayScaleFilter(currentImage, item.rRatio,item.bRatio, item.gRatio)
                setCurrentImage(grayscaleBitmap)
            }
            item.rRatio == 0.0 && item.bRatio == 0.0 && item.gRatio == 0.0 -> {

                lateinit var lutBitmap: Bitmap
                val assetManager = context.resources.assets


                if(true){
                    val inputStreamLUT = assetManager.open("grayscale.jpeg")
                    lutBitmap = BitmapFactory.decodeStream(inputStreamLUT)

                }

                val applyLutBitmap = ImageProcessor.applyLutToBitmap(currentImage , lutBitmap)

                setCurrentImage(applyLutBitmap)

            }
        }

    }


}