package com.obezhik.multiply_file_picker


import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI


class MultiplyFilePicker(private var activity: ComponentActivity, private var mRegister: ActivityResultRegistry) : DefaultLifecycleObserver {

    constructor(activity: ComponentActivity): this(activity, activity.activityResultRegistry){
        lifecycle = activity.lifecycle
        registerObservables()
    }

    constructor(fragment: Fragment): this(fragment.requireActivity(), fragment.requireActivity().activityResultRegistry) {
        lifecycle = fragment.lifecycle
       registerObservables()
    }

    private lateinit var lifecycle: Lifecycle

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

    private fun registerObservables() = lifecycle.apply {
        addObserver(this@MultiplyFilePicker)
    }

    fun removeObservables(){
        lifecycle.removeObserver(this)
    }

    private fun recipient(uris: List<Uri>){
        activity.lifecycleScope.launch {
            FileUtil.from(activity, uris).let {
                mResult.invoke(it as ArrayList<File>)
            }
        }

    }



}