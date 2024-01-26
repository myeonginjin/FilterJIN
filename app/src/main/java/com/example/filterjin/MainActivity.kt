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


        //카메라 앱 엑티비티에서 돌아올 때 안드로이드 시스템의 의해 ActivityResultCallback의 onActivity()함수 자동 실행
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //카메라 엑티비티 결과 정상인지 확인
            if (result.resultCode == Activity.RESULT_OK) {
                // 선택된 이미지의 URI를 가져옴
                val imageUri = result.data?.data
                // MainLayout의 setImage 메소드를 호출하여 imageView에 이미지를 설정
                mainLayout.loadImage(imageUri)
            }
        }

        //registerForActivityResult는 ComponentActivity의 메서드이기 때문에 Activity가 아닌 MainLayout 클래스 내에선 직접 호출 불가능. 인자로 전달해줌
        mainLayout = MainLayout(this, galleryLauncher)

        //동적으로 구현된 메인레이아웃 화면에 띄움
        setContentView(mainLayout.getMainLayout())
    }



    }


