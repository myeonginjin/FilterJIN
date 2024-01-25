package com.example.filterjin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView

class MainLayout(
    private val context: Context,
    private val galleryLauncher: ActivityResultLauncher<Intent>?
) {

    private val mainFrame = ConstraintLayout(context)
    private val topTabBar = ConstraintLayout(context)
    private val galleryBtn = Button(context)
    private val toggleFilterBtn = Button(context)
    private val saveBtn = Button(context)
    private val imageView = ImageView(context)
    private val editBar = ListViewManager(context, this).getEditBar()
    private var bitmap : Bitmap? = null


    fun loadImage(uri: Uri?) {
        // imageView에 이미지 설정
        val inputStream = uri?.let { context.contentResolver.openInputStream(it) }
        bitmap = BitmapFactory.decodeStream(inputStream)
        Log.i("?","test1234455678823982012308120381203218 $bitmap")


        imageView.setImageBitmap(bitmap)
    }

    fun getImage() : Bitmap? {
        Log.i("!!!","888888888888888\n\n\n\n\n\n\n $bitmap")
        return bitmap
    }

    fun setImage(img : Bitmap) {
        imageView.setImageBitmap(img)
    }



    fun getMainLayout() : ConstraintLayout{

        mainFrame.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
        }
        topTabBar.apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            id = ConstraintLayout.generateViewId()
        }
        mainFrame.addView(topTabBar)

        galleryBtn.apply {
            text = "Gallery"
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            id = ConstraintLayout.generateViewId()
        }
        topTabBar.addView(galleryBtn)


        toggleFilterBtn.apply {
            text = "toggle"
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            id = ConstraintLayout.generateViewId()
        }
        topTabBar.addView(toggleFilterBtn)

        saveBtn.apply {
            text = "Save"
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            id = ConstraintLayout.generateViewId()
        }
        topTabBar.addView(saveBtn)

        imageView.apply {
            setImageResource(R.drawable.test)
            layoutParams = ConstraintLayout.LayoutParams(
                1000,
                1000
            )
            id = ConstraintLayout.generateViewId()
        }
        mainFrame.addView(imageView)

        editBar.apply {
            id = ConstraintLayout.generateViewId()
        }
        mainFrame.addView(editBar)

        ConstraintSet().apply {
            clone(mainFrame)


            connect(topTabBar.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP,16)


            applyTo(mainFrame)
        }


        ConstraintSet().apply {
            clone(topTabBar)

            connect(galleryBtn.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)


            connect(saveBtn.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)


            connect(toggleFilterBtn.id, ConstraintSet.END, saveBtn.id, ConstraintSet.START, 16)


            applyTo(topTabBar)

        }

        ConstraintSet().apply {
            clone(mainFrame)

            connect(imageView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 8)
            connect(imageView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 8)
            connect(imageView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START,8)
            connect(imageView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8)


            applyTo(mainFrame)
        }

        ConstraintSet().apply {
            clone(mainFrame)

            connect(editBar.id, ConstraintSet.TOP, imageView.id, ConstraintSet.BOTTOM, 100)
            connect(editBar.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START,8)
            connect(editBar.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8)


            applyTo(mainFrame)
        }



        galleryBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher!!.launch(intent) // MainActivity에서 받은 launcher 사용
        }




        saveBtn.setOnClickListener {

            Toast.makeText(context, "saveBtn tapped", Toast.LENGTH_SHORT).show()

        }










        return  mainFrame
    }


}
