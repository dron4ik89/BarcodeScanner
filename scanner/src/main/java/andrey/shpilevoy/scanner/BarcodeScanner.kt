@file:Suppress("DEPRECATION")

package andrey.shpilevoy.scanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView

class BarcodeScanner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs), SurfaceHolder.Callback {


    init{


        View.inflate(context, R.layout.splash, this)


    }



    private val REQUEST_CODE = 77763

    private val mCameraManager: CameraManager = CameraManager(context)
    private val mPreviewCallback: CameraScanAnalysis = CameraScanAnalysis()
    private var mSurfaceView: SurfaceView? = null
    private val mFocusCallback: Camera.AutoFocusCallback =
        Camera.AutoFocusCallback { success, camera -> postDelayed(mAutoFocusTask, 1000) }
    private val mAutoFocusTask = Runnable { mCameraManager.autoFocus(mFocusCallback) }





    fun startPreview(): Boolean {

/*
        val mTextView = Button(context)
        addView(
            mTextView,
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER)
        )


        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F);
        mTextView.text = "Not work"
        mTextView.setTextColor(Color.BLACK)
*/

        /*


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf<String>(Manifest.permission.CAMERA), REQUEST_CODE)



        } else {

            try {
                mCameraManager.openDriver()
            } catch (e: Exception) {
                return false
            }

            if (mSurfaceView == null) {
                mSurfaceView = SurfaceView(context)
                addView(
                    mSurfaceView,
                    LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                )
                val holder = mSurfaceView!!.holder
                holder.addCallback(this)
                holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
            }

            startCameraPreview(mSurfaceView!!.holder)

        }*/

        return true
    }

    fun startScan() {
        mPreviewCallback.onStart()
    }

    fun stopScan() {
        mPreviewCallback.onStop()
    }

    fun stopPreview() {
        removeCallbacks(mAutoFocusTask)
        stopScan()

        mCameraManager.stopPreview()
        mCameraManager.closeDriver()
    }

    fun setScanCallback(callback: ScanCallback) {
        mPreviewCallback.setScanCallback(callback)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Log.d("LOG", "requestCode == 777")

            startPreview()
        } else {

            Log.d("LOG", "requestCode != 777")





        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val actualPreviewWidth = resources.displayMetrics.widthPixels
        val actualPreviewHeight = resources.displayMetrics.heightPixels

        val left = (actualPreviewWidth - width) / 2
        val top = (actualPreviewHeight - height) / 2
        val right = actualPreviewWidth - left
        val bottom = actualPreviewHeight - top

        if (mSurfaceView != null) {
            mSurfaceView!!.layout(-left, -top, right, bottom)
        }

    }

    private fun startCameraPreview(holder: SurfaceHolder) {
        try {
            mCameraManager.startPreview(holder, mPreviewCallback)
            mCameraManager.autoFocus(mFocusCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (holder.surface == null) {
            return
        }
        mCameraManager.stopPreview()
        startCameraPreview(holder)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    override fun onDetachedFromWindow() {
        stopPreview()
        super.onDetachedFromWindow()
    }

}