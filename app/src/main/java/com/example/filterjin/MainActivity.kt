package com.example.filterjin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private lateinit var mainLayout: MainLayout
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //카메라 앱 엑티비티에서 돌아올 때 안드로이드 시스템의 의해 ActivityResultCallback의 onActivity()함수 자동 실행
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                try {
                    if (result.resultCode == Activity.RESULT_OK) {
                        val imageUri = result.data?.data
                        if (imageUri != null) {
                            // 원본 이미지 로드 및 회전 처리
                            var rotatedOriginalBitmap: Bitmap? = null
                            contentResolver.openInputStream(imageUri)?.use { inputStream ->
                                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                                rotatedOriginalBitmap = rotateImageIfRequired(this, originalBitmap, imageUri)
                            }

                            // 리사이징 작업 및 리사이징된 이미지의 회전 처리
                            var rotatedResizedBitmap: Bitmap? = null
                            contentResolver.openInputStream(imageUri)?.use { inputStream ->
                                // 적절한 inSampleSize 계산
                                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                                BitmapFactory.decodeStream(inputStream, null, options)
                                val calRatio = calculateSampleSize(
                                    options,
                                    resources.getDimensionPixelSize(R.dimen.imgSize),
                                    resources.getDimensionPixelSize(R.dimen.imgSize)
                                )
                                // 실제 리사이징을 위해 inSampleSize 설정
                                options.apply {
                                    inJustDecodeBounds = false
                                    inSampleSize = calRatio
                                }
                                // 리사이징된 이미지 생성
                                val resizedBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri), null, options)
                                rotatedResizedBitmap = resizedBitmap?.let { rotateImageIfRequired(this, it, imageUri) }
                            }

                            // MainLayout에 원본 및 리사이징된 이미지 설정
                            if (rotatedOriginalBitmap != null && rotatedResizedBitmap != null) {
                                mainLayout.setImage(rotatedOriginalBitmap!!, rotatedResizedBitmap!!)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        //registerForActivityResult는 ComponentActivity의 메서드이기 때문에 Activity가 아닌 MainLayout 클래스 내에선 직접 호출 불가능. 인자로 전달해줌
        mainLayout = MainLayout(this, galleryLauncher)

        //동적으로 구현된 메인레이아웃 화면에 띄움
        setContentView(mainLayout.getMainLayout())
    }

    private fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(selectedImage)
        val ei = ExifInterface(inputStream!!)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(degree.toFloat()) }
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true).also {
            if (it != img) img.recycle()
            }
    }


    private fun calculateSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }



}




