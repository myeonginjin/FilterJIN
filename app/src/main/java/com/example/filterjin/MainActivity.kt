package com.example.filterjin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainLayout = ConstraintLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            id = ConstraintLayout.generateViewId()
        }


        val topTabBar = ConstraintLayout(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            id = ConstraintLayout.generateViewId()
        }
        mainLayout.addView(topTabBar)

        val galleryBtn = Button(this).apply {
            text = "gallery"
            layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            id = ConstraintLayout.generateViewId()
        }
        topTabBar.addView(galleryBtn)

        val toggleFilterBtn = Button(this).apply {
            text = "toggle"
            layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            id = ConstraintLayout.generateViewId()
        }
        topTabBar.addView(toggleFilterBtn)

        val saveBtn = Button(this).apply {
            text = "Save"
            layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            id = ConstraintLayout.generateViewId()
        }
        topTabBar.addView(saveBtn)

        val imageView = ImageView(this).apply {
            setImageResource(R.drawable.ic_launcher_background)
            layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            id = ConstraintLayout.generateViewId()
        }
        mainLayout.addView(imageView)

        ConstraintSet().apply {
            clone(mainLayout)


            connect(topTabBar.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP,16)


            applyTo(mainLayout)
        }


        ConstraintSet().apply {
            clone(topTabBar)

            connect(galleryBtn.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)


            connect(saveBtn.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)


            connect(toggleFilterBtn.id, ConstraintSet.END, saveBtn.id, ConstraintSet.START, 16)


            applyTo(topTabBar)

        }


        ConstraintSet().apply {
            clone(mainLayout)

            connect(imageView.id, ConstraintSet.TOP, topTabBar.id, ConstraintSet.BOTTOM, 80)
            connect(imageView.id, ConstraintSet.START, mainLayout.id, ConstraintSet.START, 16)
            connect(imageView.id, ConstraintSet.END, mainLayout.id, ConstraintSet.END, 16)

            applyTo(mainLayout)
        }


        setContentView(mainLayout)


        galleryBtn.setOnClickListener {

            Toast.makeText(this, "galleryBtn tapped", Toast.LENGTH_SHORT).show()


        }
        toggleFilterBtn.setOnClickListener {

            Toast.makeText(this, "toggleFilterBtn tapped", Toast.LENGTH_SHORT).show()


        }
        saveBtn.setOnClickListener {

            Toast.makeText(this, "saveBtn tapped", Toast.LENGTH_SHORT).show()


        }

    }



}