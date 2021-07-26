package com.obezhik.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.obezhik.multiply_file_picker.MultiplyFilePicker

class MainActivity : AppCompatActivity() {

    lateinit var openBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val filePicker = MultiplyFilePicker(this)

        openBtn = findViewById(R.id.open)

        openBtn.setOnClickListener {

            filePicker.selectAny{
                it.forEach { f ->
                    println(f.absolutePath + " - " + f.name + " size: " + f.totalSpace)
                }
               }
        }
    }
}