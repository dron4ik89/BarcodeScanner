@file:Suppress("DEPRECATION")

package andrey.shpilevoy.scanner

import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder

import java.io.IOException

class CameraManager(context: Context) {

    private val configuration: CameraConfiguration = CameraConfiguration(context)

    private var mCamera: Camera? = null

    val isOpen: Boolean
        get() = mCamera != null

    @Synchronized
    @Throws(Exception::class)
    internal fun openDriver() {
        if (mCamera != null) return

        mCamera = Camera.open()
        if (mCamera == null) throw IOException("The camera is occupied.")

        configuration.initFromCameraParameters(mCamera!!)

        var parameters: Camera.Parameters? = mCamera!!.parameters
        val parametersFlattened = parameters?.flatten()
        try {
            configuration.setDesiredCameraParameters(mCamera!!, false)
        } catch (re: RuntimeException) {
            if (parametersFlattened != null) {
                parameters = mCamera!!.parameters
                parameters!!.unflatten(parametersFlattened)
                try {
                    mCamera!!.parameters = parameters
                    configuration.setDesiredCameraParameters(mCamera!!, true)
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }

            }
        }

    }

    @Synchronized
    internal fun closeDriver() {
        if (mCamera != null) {
            mCamera!!.setPreviewCallback(null)
            mCamera!!.release()
            mCamera = null
        }
    }

    @Throws(IOException::class)
    internal fun startPreview(holder: SurfaceHolder, previewCallback: Camera.PreviewCallback) {
        if (mCamera != null) {
            mCamera!!.setDisplayOrientation(90)
            mCamera!!.setPreviewDisplay(holder)
            mCamera!!.setPreviewCallback(previewCallback)
            mCamera!!.startPreview()
        }
    }

    internal fun stopPreview() {
        if (mCamera != null) {
            try {
                mCamera!!.stopPreview()
            } catch (ignored: Exception) {
                // nothing.
            }

            try {
                mCamera!!.setPreviewDisplay(null)
            } catch (ignored: IOException) {
                // nothing.
            }

        }
    }

    internal fun autoFocus(callback: Camera.AutoFocusCallback) {
        if (mCamera != null)
            try {
                mCamera!!.autoFocus(callback)
            } catch (e: Exception) {
                e.printStackTrace()
            }

    }
}