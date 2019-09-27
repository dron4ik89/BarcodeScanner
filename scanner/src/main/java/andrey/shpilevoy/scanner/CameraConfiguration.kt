@file:Suppress("DEPRECATION")

package andrey.shpilevoy.scanner

import android.content.Context
import android.graphics.Point
import android.hardware.Camera
import android.util.Log
import android.view.Display
import android.view.WindowManager

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

class CameraConfiguration(private val context: Context) {

    companion object {

        private val TAG = "CameraConfiguration"

        private val MIN_PREVIEW_PIXELS = 480 * 320
        private val MAX_ASPECT_DISTORTION = 0.15
    }

    private var screenResolution: Point? = null
    var cameraResolution: Point? = null
        private set

    fun initFromCameraParameters(camera: Camera) {
        val parameters = camera.parameters
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay

        screenResolution = getDisplaySize(display)

        val screenResolutionForCamera = Point()

        screenResolutionForCamera.x = screenResolution!!.x
        screenResolutionForCamera.y = screenResolution!!.y

        if (screenResolution!!.x < screenResolution!!.y) {
            screenResolutionForCamera.x = screenResolution!!.y
            screenResolutionForCamera.y = screenResolution!!.x
        }

        cameraResolution = findBestPreviewSizeValue(parameters, screenResolutionForCamera)
    }

    private fun getDisplaySize(display: Display): Point {
        val point = Point()
        display.getSize(point)
        return point
    }

    internal fun setDesiredCameraParameters(camera: Camera, safeMode: Boolean) {
        val parameters = camera.parameters

        if (parameters == null) {
            Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.")
            return
        }

        parameters.setPreviewSize(cameraResolution!!.x, cameraResolution!!.y)
        camera.parameters = parameters

        val afterParameters = camera.parameters
        val afterSize = afterParameters.previewSize
        if (afterSize != null && (cameraResolution!!.x != afterSize.width || cameraResolution!!.y != afterSize.height)) {
            cameraResolution!!.x = afterSize.width
            cameraResolution!!.y = afterSize.height
        }
        camera.setDisplayOrientation(90)

    }

    private fun findBestPreviewSizeValue(parameters: Camera.Parameters, screenResolution: Point): Point {
        val rawSupportedSizes = parameters.supportedPreviewSizes
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default")
            val defaultSize = parameters.previewSize
            return Point(defaultSize.width, defaultSize.height)
        }

        val supportedPreviewSizes = ArrayList<Camera.Size>(rawSupportedSizes)
        Collections.sort(supportedPreviewSizes, Comparator<Camera.Size> { a, b ->
            val aPixels = a.height * a.width
            val bPixels = b.height * b.width
            if (bPixels < aPixels) {
                return@Comparator -1
            } else if (bPixels > aPixels) {
                return@Comparator 1
            }
            0
        })

        if (Log.isLoggable(TAG, Log.INFO)) {
            val previewSizesString = StringBuilder()
            for (supportedPreviewSize in supportedPreviewSizes) {
                previewSizesString.append(supportedPreviewSize.width)
                    .append('x')
                    .append(supportedPreviewSize.height)
                    .append(' ')
            }
            Log.i(TAG, "Supported preview sizes: $previewSizesString")
        }

        val screenAspectRatio = screenResolution.x.toDouble() / screenResolution.y.toDouble()

        val it = supportedPreviewSizes.iterator()
        while (it.hasNext()) {
            val supportedPreviewSize = it.next()
            val realWidth = supportedPreviewSize.width
            val realHeight = supportedPreviewSize.height
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
                it.remove()
                continue
            }

            val isCandidatePortrait = realWidth < realHeight
            val maybeFlippedWidth = if (isCandidatePortrait) realHeight else realWidth
            val maybeFlippedHeight = if (isCandidatePortrait) realWidth else realHeight

            val aspectRatio = maybeFlippedWidth.toDouble() / maybeFlippedHeight.toDouble()
            val distortion = Math.abs(aspectRatio - screenAspectRatio)
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove()
                continue
            }

            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
                val exactPoint = Point(realWidth, realHeight)
                Log.i(TAG, "Found preview size exactly matching screen size: $exactPoint")
                return exactPoint
            }
        }

        if (!supportedPreviewSizes.isEmpty()) {
            val largestPreview = supportedPreviewSizes.get(0)
            val largestSize = Point(largestPreview.width, largestPreview.height)
            Log.i(TAG, "Using largest suitable preview size: $largestSize")
            return largestSize
        }

        val defaultPreview = parameters.previewSize
        val defaultSize = Point(defaultPreview.width, defaultPreview.height)
        Log.i(TAG, "No suitable preview sizes, using default: $defaultSize")

        return defaultSize
    }
}