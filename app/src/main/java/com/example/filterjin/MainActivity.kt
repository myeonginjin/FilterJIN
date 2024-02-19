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
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

    private lateinit var mainLayout: MainLayout
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        fun resizeBitmapMaintainingAspectRatio(originalBitmap: Bitmap, maxSize: Int): Bitmap {
            val width = originalBitmap.width
            val height = originalBitmap.height

            val ratio: Float = width.toFloat() / height.toFloat()
            var finalWidth = width
            var finalHeight = height

            // 장축 기준으로 크기 조정
            if (width >= height) {
                // 너비가 높이보다 크거나 같은 경우
                if (width > maxSize) {
                    finalWidth = maxSize
                    finalHeight = (maxSize / ratio).toInt()
                }
            } else {
                // 높이가 너비보다 큰 경우
                if (height > maxSize) {
                    finalHeight = maxSize
                    finalWidth = (maxSize * ratio).toInt()
                }
            }

            return Bitmap.createScaledBitmap(originalBitmap, finalWidth, finalHeight, true)
        }



        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                try {
                    if (result.resultCode == Activity.RESULT_OK) {
                        val imageUri = result.data?.data
                        if (imageUri != null) {

                            lateinit var originalBitmap : Bitmap

                            // 원본 이미지 로드 및 회전 처리
                            lateinit var rotatedOriginalBitmap: Bitmap

                            contentResolver.openInputStream(imageUri)?.use { inputStream ->
                                originalBitmap = BitmapFactory.decodeStream(inputStream)
                                rotatedOriginalBitmap = rotateImageIfRequired(this, originalBitmap, imageUri)
                            }

                            // 리사이징 작업 및 리사이징된 이미지의 회전 처리 (보간 작업 포함)
//                            var rotatedResizedBitmap: Bitmap? = null
                            // 리사이징 작업 및 리사이징된 이미지의 회전 처리 (보간 작업 포함)


                                // 장축을 800 픽셀로 고정하여 리사이징
                                val rotatedResizedBitmap = resizeBitmapMaintainingAspectRatio(rotatedOriginalBitmap, 800)
//                                rotatedResizedBitmap = rotateImageIfRequired(this, resizedBitmap, imageUri)

                                Log.i("sizeT" , " w : ${rotatedResizedBitmap.width}  h : ${rotatedResizedBitmap.height}" )



                                // 썸네일도 동일한 로직을 적용하여 리사이징
                                val rotatedResizedThumbnail = resizeBitmapMaintainingAspectRatio(rotatedOriginalBitmap, 150) // 여기서 680을 다른 값으로 조정할 수 있음
                                Log.i("sizeT" , " w : ${rotatedResizedThumbnail.width}  h : ${rotatedResizedThumbnail.height}" )


                            // MainLayout에 원본 및 리사이징된 이미지 설정
                            mainLayout.setImage(rotatedOriginalBitmap, rotatedResizedBitmap,
                                rotatedResizedThumbnail
                            )
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


//    private fun calculateSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
//        val (height: Int, width: Int) = options.run { outHeight to outWidth }
//        var inSampleSize = 1
//
//        if (height > reqHeight || width > reqWidth) {
//            val halfHeight: Int = height / 2
//            val halfWidth: Int = width / 2
//
//            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
//                inSampleSize *= 2
//            }
//        }
//        return inSampleSize
//    }



}




