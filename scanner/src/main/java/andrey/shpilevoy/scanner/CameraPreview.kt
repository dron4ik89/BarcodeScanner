@file:Suppress("DEPRECATION")

package andrey.shpilevoy.scanner

import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout

open class CameraPreview @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs), SurfaceHolder.Callback {

    companion object{
        private var focusDelay = 1000L
        private var focus = true
    }

    private val mCameraManager: CameraManager = CameraManager(context)
    private val mPreviewCallback: CameraScanAnalysis = CameraScanAnalysis()
    private var mSurfaceView: SurfaceView? = null
    private val mFocusCallback: Camera.AutoFocusCallback =
        Camera.AutoFocusCallback { success, camera -> postDelayed(mAutoFocusTask, 1000) }
    private val mAutoFocusTask = Runnable {
        if(focus)
            mCameraManager.autoFocus(mFocusCallback)
    }


    fun startPreviewBase(): Boolean {

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

        return true
    }

    fun startScanBase() {
        mPreviewCallback.onStart()
    }

    fun stopScanBase() {
        mPreviewCallback.onStop()
    }

    fun stopPreviewBase() {
        stopFocusBase()
        stopScanBase()

        mCameraManager.stopPreview()
        mCameraManager.closeDriver()
    }

    fun isScan(): String {
        return mPreviewCallback.isAnalysis()
    }

    fun setScanCallbackBase(callback: ScanCallback) {
        mPreviewCallback.setScanCallback(callback)
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
            startFocusBase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startFocusBase(){
        focus = true
        mCameraManager.autoFocus(mFocusCallback)
    }

    fun stopFocusBase(){
        focus = false
        removeCallbacks(mAutoFocusTask)
    }

    fun setFocusDelay(delay: Long){
        focusDelay = delay
    }

    open fun setLight(mode: FlashMode = FlashMode.OFF){
        when(mode){
            FlashMode.ON -> mCameraManager.setFlashLigthOn()
            FlashMode.OFF -> mCameraManager.setFlashLightOff()
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
        stopPreviewBase()
        super.onDetachedFromWindow()
    }

}