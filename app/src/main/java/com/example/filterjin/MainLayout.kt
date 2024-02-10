package com.example.filterjin

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class MainLayout(
    private val context: Context,
    private val galleryLauncher: ActivityResultLauncher<Intent>?
) {



    private val imageViewManager = ImageViewManager(context)

    private val listViewManager = ListViewManager(context, this, imageViewManager)


    private val mainFrame = ConstraintLayout(context)
    private val topTabBar = ConstraintLayout(context)
    private val galleryBtn = Button(context)
    private val toggleFilterBtn = Button(context)
    private val saveBtn = Button(context)
    private val imageViewFrame = LinearLayout(context)

    private val imageView = imageViewManager.getImageView()

    //ListViewManager클래스로 RecyclerView 동적구현
    private val editBar = listViewManager.getEditBar()



    //사용자 기기 갤러리 통해서 받아온 이미지 이미지뷰어에 띄우기
    fun setImage(originBitmap: Bitmap, resizedBitmap : Bitmap) {


        imageViewManager.setOriginImage(originBitmap)


        imageViewManager.setImageView(resizedBitmap)


        listViewManager.setCurrentItemImage(resizedBitmap)
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


        imageViewFrame.apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            id = ConstraintLayout.generateViewId()
        }
        mainFrame.addView(imageViewFrame)


        imageView.apply {
            id = ConstraintLayout.generateViewId()
        }
        imageViewFrame.addView(imageView)


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

            connect(imageViewFrame.id, ConstraintSet.TOP, topTabBar.id, ConstraintSet.BOTTOM, 8)
            connect(imageViewFrame.id, ConstraintSet.BOTTOM, editBar.id, ConstraintSet.TOP, 24)
            connect(imageViewFrame.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START,8)
            connect(imageViewFrame.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END,8)


            applyTo(mainFrame)
        }


        ConstraintSet().apply {
            clone(mainFrame)

            connect(editBar.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 104)
            connect(editBar.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START,8)
            connect(editBar.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8)


            applyTo(mainFrame)
        }






        galleryBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher!!.launch(intent) // ActivityResultLauncher 실행
        }




        saveBtn.setOnClickListener {

            Toast.makeText(context, "saveBtn tapped", Toast.LENGTH_SHORT).show()

            val currentImage : Bitmap = imageViewManager.getCurrentImage()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                currentImage.let {
                    saveImageOnAboveAndroidQ(currentImage)
                    Toast.makeText(context, "이미지 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Q 버전 이하일 경우. 저장소 권한을 얻어온다.
                val writePermission = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                if(writePermission == PackageManager.PERMISSION_GRANTED) {
                    saveImageOnUnderAndroidQ(currentImage)
                    Toast.makeText(context, "이미지 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    val requestExternalStorageCode = 1

                    val permissionStorage = arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )

                    ActivityCompat.requestPermissions(context as Activity, permissionStorage, requestExternalStorageCode)
                }
            }
        }


        return  mainFrame
    }

    private fun saveImageOnAboveAndroidQ(bitmap: Bitmap) {
        val fileName = System.currentTimeMillis().toString() + ".png" // 파일이름 현재시간.png

        val contentValues = ContentValues()
        contentValues.apply {
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/ImageSave")
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        Log.i("size11","w : ${bitmap.width}   h : ${bitmap.height}")


        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            if (uri != null) {
                val image = context.contentResolver.openFileDescriptor(uri, "w", null)

                if (image != null) {
                    val fos = FileOutputStream(image.fileDescriptor)

                    // JPEG 형식으로 압축 (압축률 조절 가능, 100은 최대 압축)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos)

                    fos.close()

                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // saveImageOnUnderAndroidQ 함수 수정
    private fun saveImageOnUnderAndroidQ(bitmap: Bitmap) {
        val fileName = System.currentTimeMillis().toString() + ".png"
        val externalStorage = Environment.getExternalStorageDirectory().absolutePath
        val path = "$externalStorage/DCIM/imageSave"
        val dir = File(path)

        Log.i("size22","w : ${bitmap.width}   h : ${bitmap.height}")

        if (dir.exists().not()) {
            dir.mkdirs()
        }

        try {
            val fileItem = File("$dir/$fileName")
            fileItem.createNewFile()

            val fos = FileOutputStream(fileItem)

            // PNG 형식으로 압축 (압축률 조절 가능, 100은 최대 압축)
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos)

            fos.close()

            // 미디어 스캔을 통해 갤러리에 반영
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileItem)))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}
