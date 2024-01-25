package com.example.filterjin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class MainActivity : AppCompatActivity() {

    private lateinit var mainLayout: MainLayout
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 선택된 이미지의 URI를 가져옴
                val imageUri = result.data?.data
                // MainLayout의 setImage 메소드를 호출하여 imageView에 이미지를 설정
                mainLayout.loadImage(imageUri)
            }
        }

        mainLayout = MainLayout(this, galleryLauncher)
        setContentView(mainLayout.getMainLayout())
    }



    }


