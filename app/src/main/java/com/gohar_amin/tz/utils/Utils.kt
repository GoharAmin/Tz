package com.gohar_amin.tz.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.gohar_amin.tz.callback.ConfirmationCallback
import com.gohar_amin.tz.callback.ObjectCallback
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


object Utils {
    const val SENDER_MESSAGE = 1
    const val RECEIVER_MESSAGE = 2
    const val TEXT_MESSAGE = 3
    const val IMAGE_MESSAGE = 4
    var progressDialog: ProgressDialog? = null
    fun showDialog(context: Context?) {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.show()
    }

    fun dismissDialog() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }
    fun loadImage(context: Context, imageUrl: String?, iv: ImageView) {
        Glide.with(context)
            .load(imageUrl)
            .into(iv)
    }
    fun showSelectedImages(limit: Int, context: Activity) {
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        val images: ArrayList<Image> = ArrayList()
                        ImagePicker.create(context) //                                    .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                            .folderMode(true) // folder mode (false by default)
                            .toolbarFolderTitle("Folder") // folder selection title
                            .toolbarImageTitle("Tap to select") // image selection title
                            //.toolbarArrowColor(Color.RED) // Toolbar 'up' arrow color
                            //.includeVideo(true) // Show video on image picker
                            //                                    .single() // single mode
                            .multi() // multi mode (default mode)
                            .limit(limit) // max images can be selected (99 by default)
                            .showCamera(true) // show camera or not (true by default)
                            .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                            .origin(images) // original selected images, used in multi mode
                            //                        .theme(R.style.Theme) //                                    .exclude(images) // exclude anything that in image.getPath()
                            //                                    .excludeFiles(files) // same as exclude but using ArrayList<File>
                            //                                    .theme(R.style.CustomImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                            .enableLog(false) // disabling log
                            .start(10001) // start image picker activity with request code
                        Log.e("mytag", "showSelectedImages: ")
                    } else {
                        Toast.makeText(context, "Allow permissions", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun showSelectedImages(context: Activity) {
        Dexter.withContext(context)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(context,intent, 201,null)
                        } else {
                            Toast.makeText(context, "Allow permissions", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permissions: List<PermissionRequest?>?,
                            token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
    }
    fun uploadImage(
        context: Context?,
        imageUrl: Uri,
        imageName: String?,
        firebaseStorage: StorageReference,
        callback: ObjectCallback<Uri?>
    ) {
        firebaseStorage.child(imageName!!).putFile(imageUrl)
            .addOnCompleteListener { task: Task<UploadTask.TaskSnapshot?> ->
                if (task.isSuccessful) {
                    firebaseStorage.child(imageName).downloadUrl
                        .addOnCompleteListener { t: Task<Uri?> ->
                            if (t.isSuccessful) {
                                callback.onData(t.result)
                            } else {
                                Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Please try Again", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun confirmationDialog(
        context: Context?,
        msg: String?,
        callback: ConfirmationCallback<String>
    ) {
        AlertDialog.Builder(context)
            .setTitle("Are You Sure")
            .setMessage(msg)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which -> callback.onConfirm("Confirmation") })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, which ->
                    callback.onCancel()
                    dialog.dismiss()
                }).setCancelable(false).create().show()
    }
}