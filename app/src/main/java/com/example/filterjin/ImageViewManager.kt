package com.example.filterjin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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


    fun getCurrentImage () : Bitmap {
        return currentImage
    }
    fun setCurrentImage (bitmap : Bitmap) {
        currentImage = bitmap
        imageView.setImageBitmap(currentImage)
    }


}