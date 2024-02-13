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
    private var resizedImage : Bitmap = defaultImage
    private var originImage : Bitmap = defaultImage

    private var currentFilterType: String? = null
    private var currentLUTName: String? = null
    private var currentFilterR: Double = 0.0
    private var currentFilterG: Double = 0.0
    private var currentFilterB: Double = 0.0

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

    fun getCurrentImage(): Bitmap {

            Log.i("test2","$currentLUTName")

            when (currentFilterType){
                "Ratio" -> {
                    return ImageProcessor.applyRatioFilter(originImage, currentFilterR, currentFilterG, currentFilterB)
                }
                "LUT" -> {
                    val assetManager = context.resources.assets
                    val inputStreamLUT = currentLUTName?.let { assetManager.open(it) }
                    val lutBitmap = BitmapFactory.decodeStream(inputStreamLUT)
                    return ImageProcessor.applyLutToBitmap(originImage, lutBitmap)
                }
            }
        return originImage
    }

    fun loadGalleryImage(bitmap: Bitmap){
        resizedImage = bitmap
        imageView.setImageBitmap(bitmap)
    }


    private fun setImageView (bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)
    }

    fun setOriginImage (bitmap : Bitmap) {
        originImage = bitmap
    }

    fun applyFilter(item: FilterItem) {

        Log.i("test","$currentLUTName     ${item.name}")

        if (currentLUTName.equals(item.name)){
            setImageView(resizedImage)
            currentFilterType = null
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
            currentFilterR = item.rRatio
            currentFilterG = item.gRatio
            currentFilterB = item.bRatio
        }


    }


}