package dev.k504.cameraenginendk

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var bitmapReady = true
    var cameraPermistion = false;
    var storagePermistion = false;
    var backCamera = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.setContentView(R.layout.activity_main)

        getPermistion()

        takePhoto_button.setOnClickListener {
            TakePhoto()
        }

        info_button.setOnClickListener {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("This is a demo version using Android Camera Native library!")
                .setMessage("This demo created via official google repositories and use maximum camera characteristics in your phone. © 2019")
                .setCancelable(false)
                .setNegativeButton("Ok! Thanks", object:DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.cancel()
                    }
                })
            val dialog = alert.create()
            dialog.show();
        }

        rotate_button.setOnClickListener {
            if(backCamera == true) backCamera = false
            else backCamera = true
            OpenCamera(backCamera, this)
            cameraPreview();
        }

        if(cameraPermistion)
        {
            OpenCamera(backCamera, this)
            cameraPreview()
        }

    }


    fun imagePreview(bitmapArray: IntArray, w: Int, h:Int): Boolean {
        if(bitmapReady)
        {
            bitmapReady = false
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(bitmapArray, 0, w, 0,0, w, h)
            runOnUiThread {
                imageView.setImageBitmap(bitmap)
            }
            bitmapReady = true
        }
        return bitmapReady
    }

    fun getPermistion()
    {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(
            object:MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted())
                    {
                        cameraPermistion = true;
                        storagePermistion = true;
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }
        ).onSameThread().check()
    }

    external fun OpenCamera(isBack: Boolean, activity: MainActivity)
    external fun cameraPreview()
    external fun TakePhoto()
    external fun deleteCamera()

    companion object {

        init{
            System.loadLibrary("ndk_camera")
        }
    }
}
