package com.obezhik.multiply_file_picker


import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI


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
        activity.lifecycleScope.launch {
            FileUtil.from(activity, uris).let {
                mResult.invoke(it as ArrayList<File>)
            }
        }

    }



}