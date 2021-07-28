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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val filePicker = MultiplyFilePicker(this)

        openBtn = findViewById(R.id.open)

        openBtn.setOnClickListener {

      /*  filePicker
                .checkPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)){
                println(it)
            }*/

          filePicker.takePhoto(applicationContext.packageName + ".provider"){
                when(it){
                    is File -> { println(it.name)   }
                    is String -> { println(it) }
                }
            }

          /*  filePicker.selectAnyFiles{
                it.forEach { f ->
                    println(f.absolutePath + " - " + f.name + " size: " + f.totalSpace)
                }
               }*/
        }
    }
}