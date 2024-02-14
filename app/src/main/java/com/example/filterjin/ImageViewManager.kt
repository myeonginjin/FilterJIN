package com.example.filterjin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

class ImageViewManager (private val context : Context){

    private var imageView : ImageView = ImageView(context)
    private var defaultImage : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_image)
    var originImage : Bitmap = defaultImage
    var resizedImage : Bitmap = defaultImage
    private var currentViewImage : Bitmap = defaultImage
    var currentFilterType: String? = null
    var currentFilterName: String? = null
    var currentLUTName: String? = null
    var currentFilterR: Double = 0.0
    var currentFilterG: Double = 0.0
    var currentFilterB: Double = 0.0

    fun getImageView(): ImageView {
        imageView.apply {
            setImageBitmap(resizedImage)
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        return imageView
    }

//    fun getCurrentImage(): Bitmap {
//
//            when (currentFilterType){
//                "Ratio" -> {
//                    return ImageProcessor.applyRatioFilter(originImage, currentFilterR, currentFilterG, currentFilterB)
//                }
//                "LUT" -> {
//                    val assetManager = context.resources.assets
//                    val inputStreamLUT = currentLUTName?.let { assetManager.open(it) }
//                    val lutBitmap = BitmapFactory.decodeStream(inputStreamLUT)
//                    return ImageProcessor.applyLutToBitmap(originImage, lutBitmap)
//                }
//            }
//        return originImage
//    }

    fun loadGalleryImage(bitmap: Bitmap){
        resizedImage = bitmap
        currentViewImage = bitmap
        imageView.setImageBitmap(bitmap)
    }


    private fun setImageView (bitmap: Bitmap) {
        currentViewImage = bitmap
        imageView.setImageBitmap(bitmap)
    }

//    fun setOriginImage (bitmap : Bitmap) {
//        originImage = bitmap
//    }

    fun toggleImage (tap : Boolean){
        if(tap){
            imageView.setImageBitmap(resizedImage)
        }
        else{
            imageView.setImageBitmap(currentViewImage)
        }

    }

    fun applyFilter(item: FilterItem) {

        Log.i("test","$currentFilterName     ${item.name}")

        if (currentFilterName.equals(item.name)){
            setImageView(resizedImage)
            currentFilterType = null
            currentFilterName = null
            currentLUTName = null
            currentFilterR = 0.0
            currentFilterG = 0.0
            currentFilterB = 0.0
        }

        else{
            when (item.type) {
                "Ratio" -> {

                    val  grayscaleBitmap = ImageProcessor.applyRatioFilter(resizedImage, item.rRatio,item.bRatio, item.gRatio)
                    setImageView(grayscaleBitmap)
                }
                "LUT" -> {

                    lateinit var lutBitmap: Bitmap
                    val assetManager = context.resources.assets
                    val fileName : String = item.lut


                    val inputStreamLUT = assetManager.open(fileName)
                    lutBitmap = BitmapFactory.decodeStream(inputStreamLUT)


                    val applyLutBitmap = ImageProcessor.applyLutToBitmap(resizedImage , lutBitmap)

                    setImageView(applyLutBitmap)
                }
            }
            currentLUTName = item.lut
            currentFilterType = item.type
            currentFilterName = item.name
            currentFilterR = item.rRatio
            currentFilterG = item.gRatio
            currentFilterB = item.bRatio
        }


    }


}