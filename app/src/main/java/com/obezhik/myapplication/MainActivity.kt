package com.obezhik.myapplication

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import com.obezhik.multiply_file_picker.MultiplyFilePicker
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var openBtn: Button
    lateinit var filePicker: MultiplyFilePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        filePicker = MultiplyFilePicker(this)

        openBtn = findViewById(R.id.open)

        openBtn.setOnClickListener {

            filePicker.takePhoto(applicationContext.packageName + ".provider"){
                if(it.success){
                    it.photo?.name
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        filePicker.removeObservables()
    }
}