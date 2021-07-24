package com.obezhik.multiply_file_picker

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.File

class MultiplyFilePicker(private var activity: ComponentActivity, private var mRegister: ActivityResultRegistry) : DefaultLifecycleObserver {

    constructor(activity: ComponentActivity): this(activity, activity.activityResultRegistry){
        activity.lifecycle.addObserver(this)
    }

    private lateinit var mLauncher: ActivityResultLauncher<String>

    private lateinit var mResult: (files: ArrayList<File>) -> Unit

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        mLauncher = mRegister.register(
            "filePicker",
            owner,
            ActivityResultContracts.GetMultipleContents(),
            this::recipient
        )

    }

    fun selectImage(callBack: (files: ArrayList<File>) -> Unit){
        mResult = callBack
        mLauncher.launch("image/*")
    }

    fun selectAny(callBack: (files: ArrayList<File>) -> Unit){
        mResult = callBack
        mLauncher.launch("*/*")
    }

    private fun recipient(uris: List<Uri>){
        uris.map { File(it.path) }.let {
            mResult.invoke(it as ArrayList<File>)
        }

    }
}