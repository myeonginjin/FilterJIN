package com.example.filterjin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

class ImageViewManager (private val context : Context){

    private var imageView : ImageView = ImageView(context)
    private var defaultImage : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_image)
    private var resizedImage : Bitmap = defaultImage
    private var originImage : Bitmap = defaultImage
    

    fun getImageView(): ImageView {
        imageView.apply {
            setImageBitmap(resizedImage)
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            id = ConstraintLayout.generateViewId()
        }
        return imageView
    }

    fun getCurrentImage(): Bitmap {

        lateinit var lutBitmap: Bitmap
        val assetManager = context.resources.assets
        val inputStreamLUT = assetManager.open("grayscale.jpeg")
        lutBitmap = BitmapFactory.decodeStream(inputStreamLUT)

        return ImageProcessor.applyLutToBitmap(originImage, lutBitmap)
    }


    fun setImageView (bitmap: Bitmap) {
        resizedImage = bitmap
        imageView.setImageBitmap(resizedImage)
    }

    fun setOriginImage (bitmap : Bitmap) {
        originImage = bitmap
    }

    fun applyFilter(item: FilterItem) {

        when {
            item.name=="GrayScale" -> {

                val  grayscaleBitmap = ImageProcessor.applyGrayScaleFilter(resizedImage, item.rRatio,item.bRatio, item.gRatio)
                setImageView(grayscaleBitmap)
            }
            item.rRatio == 0.0 && item.bRatio == 0.0 && item.gRatio == 0.0 -> {

                lateinit var lutBitmap: Bitmap
                val assetManager = context.resources.assets


                if(true){
                    val inputStreamLUT = assetManager.open("grayscale.jpeg")
                    lutBitmap = BitmapFactory.decodeStream(inputStreamLUT)

                }

                val applyLutBitmap = ImageProcessor.applyLutToBitmap(resizedImage , lutBitmap)

                setImageView(applyLutBitmap)

            }
        }

    }


}