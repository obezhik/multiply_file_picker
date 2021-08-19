package com.obezhik.multiply_file_picker


import android.Manifest
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class MultiplyFilePicker(private var activity: ComponentActivity, private var mRegister: ActivityResultRegistry) : DefaultLifecycleObserver {

    constructor(activity: ComponentActivity): this(activity, activity.activityResultRegistry){
        lifecycle = activity.lifecycle
        registerObservables()
    }

    constructor(fragment: Fragment): this(fragment.requireActivity(), fragment.requireActivity().activityResultRegistry) {
        lifecycle = fragment.lifecycle
       registerObservables()
    }

    private val IMAGE_EXTENSION = ".jpg"

    private lateinit var lifecycle: Lifecycle

    private lateinit var mFileLauncher: ActivityResultLauncher<String>
    private lateinit var mPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var mCameraLauncher: ActivityResultLauncher<Uri>

    private lateinit var mFilesResult: (files: ArrayList<File>) -> Unit
    private lateinit var mPhotoResult: (result: PhotoResult) -> Unit
    private lateinit var mPermissionsResult: (result: Map<String, Boolean>) -> Unit
    private lateinit var mPermissionResult: (success:  Boolean) -> Unit

    private lateinit var photoFile: File

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        mPermissionsLauncher = mRegister.register(
            "permissions",
            owner,
            ActivityResultContracts.RequestMultiplePermissions()
        ){
            mPermissionsResult.invoke(it)
        }

        mPermissionLauncher = mRegister.register(
            "permission",
            owner,
            ActivityResultContracts.RequestPermission()
        ){
            mPermissionResult.invoke(it)
        }

        mFileLauncher = mRegister.register(
            "filePicker",
            owner,
            ActivityResultContracts.GetMultipleContents(),
            this::recipientFiles
        )

        mCameraLauncher = mRegister.register(
            "cameraPicker",
            owner,
            ActivityResultContracts.TakePicture(),
            this::recipientPhoto
        )


    }

    private fun registerObservables() = lifecycle.apply {
        addObserver(this@MultiplyFilePicker)
    }

    fun removeObservables(){
        lifecycle.removeObserver(this)
    }

    //<editor-fold desc="takeFile">
    fun selectImage(callBack: (files: ArrayList<File>) -> Unit){
        mFilesResult = callBack
        mFileLauncher.launch("image/*")
    }

    fun selectAnyFiles(callBack: (files: ArrayList<File>) -> Unit){
        mFilesResult = callBack
        mFileLauncher.launch("*/*")
    }

    private fun recipientFiles(uris: List<Uri>){
        activity.lifecycleScope.launch(Dispatchers.IO) {
            FileUtil.from(activity, uris).let {
                withContext(Dispatchers.Main){
                    mFilesResult.invoke(it)
                }
                           }
        }
    }
    //</editor-fold>

    //<editor-fold desc="permissions">

    fun checkPermissions(permission: Array<String>, callback: (result: Map<String, Boolean>) -> Unit ){
        mPermissionsResult = callback

        mPermissionsLauncher.launch(permission)

    }

    fun checkPermission(permission: String, callback: (success: Boolean) -> Unit ){
        mPermissionResult = callback

        mPermissionLauncher.launch(permission)
    }

    //</editor-fold>

    //<editor-fold desc="takePicture">

    private fun recipientPhoto(success: Boolean){
        when(success){
            true -> {
                mPhotoResult.invoke(PhotoResult(true, photoFile))
            }
            false -> {
                mPhotoResult.invoke(PhotoResult(false, null))
            }
        }
    }

    fun takePhoto(authority: String, callBack: (result: PhotoResult) -> Unit){
        checkPermission(Manifest.permission.CAMERA){ takeIf { true }
          //  if(it){
                try {
                    mPhotoResult = callBack
                    photoFile = FileUtil.createTempFile(getDateFormat().plus(IMAGE_EXTENSION))
                    mCameraLauncher.launch(
                        FileProvider.getUriForFile(
                            activity,
                            authority,
                            photoFile
                        )
                    )
                } catch (ex: IOException) {
                    ex.message?.let { callBack.invoke(PhotoResult(false, null)) }
                }
         //   }
        }
    }

    //</editor-fold>

    fun getDateFormat(): String {
        val format = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);
        return format.format(Date())
    }

    data class PhotoResult(val  success: Boolean, val photo: File?)
}