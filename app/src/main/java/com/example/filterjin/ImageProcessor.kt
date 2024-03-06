package com.example.filterjin

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.ceil
import kotlin.math.floor

object ImageProcessor {

    fun applyRatioFilter(
        originalBitmap: Bitmap,
        rRatio: Double,
        gRatio: Double,
        bRatio: Double
    ): Bitmap {
        // Get the width and height of the bitmap
        val width: Int = originalBitmap.width
        val height: Int = originalBitmap.height

        // Get the pixels of the bitmap
        val pixels = IntArray(width * height)
        originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // Apply grayscale filter
        for (i in 0 until width * height) {
            val pixel = pixels[i]

            // Extract RGB values
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)

            // Calculate grayscale value
            val gray1 = (red * rRatio + green * gRatio + blue * bRatio).toInt()

            // Create new RGB value (all color channels have the same value)
            pixels[i] = Color.rgb(gray1, gray1, gray1)
        }

        // Create a new bitmap with the modified pixels

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    private lateinit var lutPixels : IntArray

     fun applyLutToBitmap(src: Bitmap, lutBitmap: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val srcPixels = IntArray(width * height)
        src.getPixels(srcPixels, 0, width, 0, 0, width, height)

        val lutSize = lutBitmap.width // LUT 이미지의 가로 또는 세로 크기
        lutPixels = IntArray(lutSize * lutSize)
        lutBitmap.getPixels(lutPixels, 0, lutSize, 0, 0, lutSize, lutSize)


        // 원본 이미지의 각 픽셀에 대해 LUT를 이용하여 새 색상을 적용
        for (index in srcPixels.indices) {
            val pixel = srcPixels[index]
            val r = (pixel shr 16) and 0xff
            val g = (pixel shr 8) and 0xff
            val b = pixel and 0xff

            // LUT 인덱스 계산
            val lutPixel = getLutPixel(r, g, b)

            srcPixels[index] = lutPixel
        }


        // 필터가 적용된 비트맵을 생성하고 반환
        val filteredBitmap = Bitmap.createBitmap(width, height, src.config)
        filteredBitmap.setPixels(srcPixels, 0, width, 0, 0, width, height)
        return filteredBitmap
    }


    // LUT이미지 파일을 통해 필터링한 픽셀을 반환해주는 함수
    private fun getLutPixel(red: Int, green: Int, blue: Int) : Int{

        val r = red / 4.0
        val g = green / 4.0
        val b = blue / 4.0

        // R,G,B 값을 각각 내림했을 때 얻게 되는 LUT의 픽셀 인덱스
        val floorLutIndex = getLutIndex(floor(r).toInt(), floor(g).toInt(), floor(b).toInt())

        // R,G,B 값을 각각 올림했을 때 얻게 되는 LUT의 픽셀 인덱스
        val ceilLutIndex : Int


        // 올림한 R,G,B 값 중, LUT이미지 파일의 인덱스 범위(0~63)를 벗어 나는 것이 없는 경우
        if (ceil(r) <= 63 &&  ceil(g) <= 63 && ceil(b) <= 63){
            ceilLutIndex = getLutIndex(ceil(r).toInt(), ceil(g).toInt(), ceil(b).toInt())
        }

        // 올림한 R,G,B 값 중, LUT이미지 파일의 인덱스 범위(0~63)를 벗어 나는 것이 있는 경우
        else{
            var adjustR = ceil(r)
            var adjustG = ceil(g)
            var adjustB = ceil(b)

            if (ceil(r) > 63){
                adjustR = 63.0
            }
            if (ceil(g) > 63){
                adjustG = 63.0
            }
            if (ceil(b) > 63){
                adjustB = 63.0
            }

            // LUT이미지 파일의 인덱스 범위의 최대값인 63으로 조정
            ceilLutIndex = getLutIndex(adjustR.toInt(), adjustG.toInt(), adjustB.toInt())
        }


        // R,G,B 각각 값을 내림했을 때 얻게되는 각각의 LUT 픽셀값
        val floorLutPixel = lutPixels[floorLutIndex]

        // R,G,B 각각 값을 올림했을 때 얻게되는 각각의 LUT 픽셀값
        val ceilLutPixel = lutPixels[ceilLutIndex]

        // 4배 축소된 LUT이미지(63*63*63) 파일로 인해 선형 보간법 적용
        val outPutR = bilinearInterpolation(Color.red(floorLutPixel), Color.red(ceilLutPixel), r - r.toInt())
        val outPutG = bilinearInterpolation(Color.green(floorLutPixel), Color.green(ceilLutPixel), g - g.toInt())
        val outPutB = bilinearInterpolation(Color.blue(floorLutPixel), Color.blue(ceilLutPixel), b - b.toInt())


        return Color.rgb(outPutR, outPutG, outPutB)
    }

    private fun getLutIndex(red: Int, green: Int, blue: Int): Int {

        var index = 0

        index += (blue / 8) * (64 * 64 * 8)
        index += green * 8 * 64
        index += (blue % 8) * 64 + red

        return index
    }

    private  fun bilinearInterpolation (color1 : Int , color2 : Int,  ratio : Double ): Int {

        val ratio1 : Double = 1.0 - ratio
        val outputColor : Double = color1 * ( ratio1 ) + color2 * ratio

        return outputColor.toInt()

    }

}




