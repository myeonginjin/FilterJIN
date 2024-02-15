package com.example.filterjin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.service.controls.templates.ThumbnailTemplate
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

//    private val bottomBar = setupBottomBar()

    private val categoryBar = setupCategoryBar()
    //ListViewManager클래스로 RecyclerView 동적구현
    private val editBar = listViewManager.getEditBar()

    private val bottomBar = setupBottomBar()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var progressDialog: Dialog
    private var imageProcessingJob: Job? = null // 이미지 처리 작업을 관리할 Job 변수




    //사용자 기기 갤러리 통해서 받아온 이미지 이미지뷰어에 띄우기
    fun setImage(originBitmap: Bitmap, resizedBitmap : Bitmap, thumbnailBitmap: Bitmap) {


        imageViewManager.originImage = originBitmap


        imageViewManager.loadGalleryImage(resizedBitmap)



        listViewManager.setCurrentItemImage(thumbnailBitmap)
    }



    init {
        createProgressDialog()
        setupGalleryButtonWithTooltip()
    }


    private fun setupGalleryButtonWithTooltip() {
        // SharedPreferences를 사용하여 최초 클릭 여부 확인
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstGalleryClick", true)

        if (isFirstLaunch) {
            // 최초 클릭 시 안내 말풍선 표시
            galleryBtn.post { // 뷰가 완전히 로드된 후에 Toast 메시지를 표시
                Toast.makeText(context, "사진을 불러와주세요", Toast.LENGTH_LONG).show()
            }
            // SharedPreferences 업데이트
            with(sharedPreferences.edit()) {
                putBoolean("isFirstGalleryClick", false)
                apply()
            }
        }

        galleryBtn.setOnClickListener {
            // 기존 갤러리 런처 코드
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher?.launch(intent)
        }
    }

    private fun createProgressDialog() {
        progressDialog = Dialog(context).apply {
            setContentView(R.layout.custom_progress_dialog) // 커스텀 레이아웃 설정
            setCancelable(false) // 뒤로 가기 버튼으로 취소 불가능하도록 설정

//            // 취소 버튼 리스너 설정
//            val closeButton = findViewById<ImageView>(R.id.closeButton)
//            closeButton.setOnClickListener {
//                progressDialog.dismiss()  // 다이얼로그 먼저 닫기
//                imageProcessingJob?.cancel() // 이미지 처리 작업 취소
//            }
        }
    }


    private fun setupCategoryBar(): HorizontalScrollView {
        val horizontalScrollView = HorizontalScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            isHorizontalScrollBarEnabled = false
        }
        val categoryList = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, // LinearLayout의 너비를 WRAP_CONTENT로 변경
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val categories = listOf("ALL") + listViewManager.getUniqueCategories()

        setupCategoryButtons(categories, categoryList)
        horizontalScrollView.addView(categoryList) // LinearLayout을 HorizontalScrollView에 추가
        return horizontalScrollView
    }



    private fun setupCategoryButtons(categories: List<String>, categoryList: LinearLayout) {
        categories.forEach { category ->
            val button = Button(context).apply {
                text = category
                setBackgroundColor(Color.TRANSPARENT) // 버튼 배경을 투명하게 설정
                setTextColor(Color.WHITE) // 텍스트 색상을 흰색으로 설정
                textSize = 12f // 텍스트 크기 설정

                // "Roboto Medium" 폰트 스타일 적용
                typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)

//                val paddingInDp = 8 // dp 단위
//                val density = context.resources.displayMetrics.density
//                val paddingInPx = (paddingInDp * density).toInt()
//                setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)

                setOnClickListener {
                    // 선택된 카테고리의 첫 아이템으로 스크롤하는 로직 등 구현
                    listViewManager.scrollToCategory(category)
                }
            }
            categoryList.addView(button)
        }
    }

    private fun setupBottomBar(): ConstraintLayout {
        // bottomBar 레이아웃 생성
        val bottomBarLayout = ConstraintLayout(context).apply {

            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.parseColor("#424242"))
        }
        return bottomBarLayout
    }


    @SuppressLint("ClickableViewAccessibility")
    fun getMainLayout() : ConstraintLayout{



        mainFrame.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor( Color.parseColor("#2C2C2C"))
        }



        topTabBar.apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            id = ConstraintLayout.generateViewId()
        }
        mainFrame.addView(topTabBar)



        val sizeInDp = 32
        val density = context.resources.displayMetrics.density
        val sizeInPx = (sizeInDp * density).toInt()


        galleryBtn.apply {
            val iconSize = (24 * context.resources.displayMetrics.density).toInt() // 아이콘 크기를 24dp로 설정
            val padding = (sizeInPx - iconSize) / 2 // 아이콘을 중앙에 위치시키기 위한 패딩 계산

            layoutParams = ConstraintLayout.LayoutParams(sizeInPx, sizeInPx).apply {
                setMargins(8, 8, 8, 8) // 버튼 마진 설정
            }
            id = ConstraintLayout.generateViewId()

            // 버튼의 배경으로 둥근 모서리 설정
            val backgroundDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.DKGRAY) // 버튼의 배경색 설정
                cornerRadius = 12 * context.resources.displayMetrics.density // 둥근 모서리 반지름 설정
            }
            background = backgroundDrawable

            // 아이콘 Drawable 가져오기 및 설정
            val drawable = ContextCompat.getDrawable(context, R.drawable.baseline_image_search_24)?.apply {
                setBounds(0, 0, iconSize, iconSize) // 아이콘 크기 설정
                setTint(Color.WHITE) // 아이콘 색상을 흰색으로 설정
            }
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            setPadding(padding, padding, padding, padding) // 아이콘을 중앙에 위치시키기 위한 패딩 설정
            gravity = Gravity.CENTER // 내용 중앙 정렬
            text = "" // 버튼 텍스트 없음
        }
        topTabBar.addView(galleryBtn)


        toggleFilterBtn.apply {
            val iconSize = (24 * context.resources.displayMetrics.density).toInt() // 아이콘 크기를 24dp로 설정
            val padding = (sizeInPx - iconSize) / 2 // 아이콘을 중앙에 위치시키기 위한 패딩 계산

            layoutParams = ConstraintLayout.LayoutParams(sizeInPx, sizeInPx).apply {
                setMargins(8, 8, 8, 8) // 버튼 마진 설정
            }
            id = ConstraintLayout.generateViewId()

            // 버튼의 배경으로 둥근 모서리 설정
            val backgroundDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.DKGRAY) // 버튼의 배경색 설정
                cornerRadius = 12 * context.resources.displayMetrics.density // 둥근 모서리 반지름 설정
            }
            background = backgroundDrawable

            // 아이콘 Drawable 가져오기 및 설정
            val drawable = ContextCompat.getDrawable(context, R.drawable.baseline_autorenew_24)?.apply {
                setBounds(0, 0, iconSize, iconSize) // 아이콘 크기 설정
                setTint(Color.WHITE) // 아이콘 색상을 흰색으로 설정
            }
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            setPadding(padding, padding, padding, padding) // 아이콘을 중앙에 위치시키기 위한 패딩 설정
            gravity = Gravity.CENTER // 내용 중앙 정렬
            text = "" // 버튼 텍스트 없음
        }
        topTabBar.addView(toggleFilterBtn)


        saveBtn.apply {
            val iconSize = (24 * context.resources.displayMetrics.density).toInt() // 아이콘 크기를 24dp로 설정
            val padding = (sizeInPx - iconSize) / 2 // 아이콘을 중앙에 위치시키기 위한 패딩 계산

            layoutParams = ConstraintLayout.LayoutParams(sizeInPx, sizeInPx).apply {
                setMargins(8, 8, 8, 8) // 버튼 마진 설정
            }
            id = ConstraintLayout.generateViewId()

            // 버튼의 배경으로 둥근 모서리 설정
            val backgroundDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.DKGRAY) // 버튼의 배경색 설정
                cornerRadius = 12 * context.resources.displayMetrics.density // 둥근 모서리 반지름 설정
            }
            background = backgroundDrawable

            // 아이콘 Drawable 가져오기 및 설정
            val drawable = ContextCompat.getDrawable(context, R.drawable.baseline_archive_24)?.apply {
                setBounds(0, 0, iconSize, iconSize) // 아이콘 크기 설정
                setTint(Color.WHITE) // 아이콘 색상을 흰색으로 설정
            }
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            setPadding(padding, padding, padding, padding) // 아이콘을 중앙에 위치시키기 위한 패딩 설정
            gravity = Gravity.CENTER // 내용 중앙 정렬
            text = "" // 버튼 텍스트 없음
        }
        topTabBar.addView(saveBtn)


        imageViewFrame.apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            )
            id = ConstraintLayout.generateViewId()
        }
        mainFrame.addView(imageViewFrame)


        imageView.apply {
            id = ConstraintLayout.generateViewId()
        }
        imageViewFrame.addView(imageView)



        bottomBar.apply {
            id = ConstraintLayout.generateViewId()
        }
        mainFrame.addView(bottomBar)


        categoryBar.apply {
            id = ConstraintLayout.generateViewId()
        }
        bottomBar.addView(categoryBar)



        editBar.apply {
            id = ConstraintLayout.generateViewId()
        }
        bottomBar.addView(editBar)




        ConstraintSet().apply {
            clone(mainFrame)


            connect(topTabBar.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP,32)


            applyTo(mainFrame)
        }


        ConstraintSet().apply {
            clone(topTabBar)

            connect(galleryBtn.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 24)


            connect(saveBtn.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 24)


            connect(toggleFilterBtn.id, ConstraintSet.END, saveBtn.id, ConstraintSet.START, 24)


            applyTo(topTabBar)

        }


        ConstraintSet().apply {
            clone(mainFrame)

            connect(imageViewFrame.id, ConstraintSet.TOP, topTabBar.id, ConstraintSet.BOTTOM, 8)
            connect(imageViewFrame.id, ConstraintSet.BOTTOM, bottomBar.id, ConstraintSet.TOP, 24)
            connect(imageViewFrame.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START,8)
            connect(imageViewFrame.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END,8)


            applyTo(mainFrame)
        }

//        ConstraintSet().apply {
//            clone(mainFrame)
//
//            connect(categoryBar.id, ConstraintSet.BOTTOM, editBar.id, ConstraintSet.TOP, 0)
//            connect(categoryBar.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START,0)
//            connect(categoryBar.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END,0)
//
//
//            applyTo(mainFrame)
//        }
//
//        ConstraintSet().apply {
//            clone(mainFrame)
//
//            connect(editBar.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 104)
//            connect(editBar.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START,8)
//            connect(editBar.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8)
//
//
//            applyTo(mainFrame)
//        }

        ConstraintSet().apply {
            clone(bottomBar)

            // categoryBar를 bottomBar의 상단에 위치시키고 상단 마진 설정
            connect(categoryBar.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 8) // 여기서 dpToPx는 dp 단위를 픽셀로 변환하는 함수입니다.

            // editBar를 bottomBar의 하단에 위치시키고 하단 마진 설정
            connect(editBar.id, ConstraintSet.TOP, categoryBar.id, ConstraintSet.BOTTOM, 8)

            // 추가적으로 categoryBar와 editBar 사이의 제약 조건을 설정할 수 있습니다.
            connect(editBar.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 40)

            applyTo(bottomBar)

        }


        ConstraintSet().apply {
            clone(mainFrame)

            connect(bottomBar.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 128)
            connect(bottomBar.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START,8)
            connect(bottomBar.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END,8)


            applyTo(mainFrame)
        }


        galleryBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher!!.launch(intent) // ActivityResultLauncher 실행
        }


        saveBtn.setOnClickListener {
            progressDialog.show() // 프로그레스 다이얼로그 표시
            val startTime = System.currentTimeMillis() // 처리 시작 시간 기록
            imageProcessingJob = coroutineScope.launch {

                var processedBitmap: Bitmap? = null
                try {
                    // 이미지 처리 로직 시작
                    processedBitmap = withContext(Dispatchers.Default) {
                        // 이미지 처리 로직
                        val currentFilterType = imageViewManager.currentFilterType
                        val originImage = imageViewManager.originImage
                        when (currentFilterType) {
                            "Ratio" -> {
                                ImageProcessor.applyRatioFilter(
                                    originalBitmap = originImage,
                                    rRatio = imageViewManager.currentFilterR,
                                    gRatio = imageViewManager.currentFilterG,
                                    bRatio = imageViewManager.currentFilterB
                                ).also {
                                    // 취소 상태 확인
                                    if (!isActive) return@withContext null
                                }
                            }
                            "LUT" -> {
                                val lutBitmap = BitmapFactory.decodeStream(context.assets.open(imageViewManager.currentLUTName!!))
                                ImageProcessor.applyLutToBitmap(originImage, lutBitmap).also {
                                    // 취소 상태 확인
                                    if (!isActive) return@withContext null
                                }
                            }
                            else -> originImage
                        }
                    }

                    // 이미지 저장 로직
                    processedBitmap?.let {
                        withContext(Dispatchers.IO) {
                            // 취소 상태 확인
                            if (!isActive) return@withContext

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                saveImageOnAboveAndroidQ(it)
                            } else {
                                saveImageOnUnderAndroidQ(it)
                            }
                        }
                    }

                    // 처리 완료 후 최소 표시 시간 보장 로직
                    val elapsedTime = System.currentTimeMillis() - startTime
                    if (elapsedTime < 1000) {
                        delay(1000 - elapsedTime) // 최소 1초간 대기
                    }

                } catch (e: Exception) {
                    // 에러 처리
                } finally {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        if (!isActive) {
                            Toast.makeText(context, "이미지 처리가 취소되었습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            processedBitmap?.let {
                                Toast.makeText(context, "이미지 저장 완료", Toast.LENGTH_SHORT).show()
                            } ?: Toast.makeText(context, "이미지 처리 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


        /*
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

         */


        toggleFilterBtn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 사용자가 버튼을 꾹 누르고 있을 때의 동작
                    // 원본 이미지 표시
                    Log.i("test44","?@")
//                    Toast.makeText(context,"tapped",Toast.LENGTH_SHORT).show()
                    imageViewManager.toggleImage(true)

                    true // 이벤트 처리 완료
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 사용자가 버튼에서 손을 떼거나 취소했을 때의 동작
                    // 필터 적용된 이미지 표시
//                    Toast.makeText(context,"no tapped",Toast.LENGTH_SHORT).show()
                    imageViewManager.toggleImage(false)

                    true // 이벤트 처리 완료
                }
                else -> false // 이외의 이벤트는 처리하지 않음
            }
        }



        return  mainFrame
    }

    @SuppressLint("Recycle")
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