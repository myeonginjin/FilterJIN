package com.example.filterjin

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ListViewManager (context : Context) {
    private  val editBar = RecyclerView(context)

    fun getEditBar() : RecyclerView{

        editBar.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                400
            )
        }
        editBar.setBackgroundColor(Color.GRAY)

        return  editBar

    }
}