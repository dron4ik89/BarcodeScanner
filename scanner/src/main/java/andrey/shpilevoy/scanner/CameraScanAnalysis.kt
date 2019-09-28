@file:Suppress("DEPRECATION")

package andrey.shpilevoy.scanner

import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log

import com.yanzhenjie.zbar.Config
import com.yanzhenjie.zbar.Image
import com.yanzhenjie.zbar.ImageScanner

import java.util.concurrent.Executors

internal class CameraScanAnalysis : Camera.PreviewCallback {

    companion object{
        internal var format: ScannerFormat = ScannerFormat.PREVIEW
        internal var delay : Long = 0
        internal var bcFormats : ArrayList<BarcodeFormat> = ArrayList()
        init{
            bcFormats.addAll(BarcodeFormat.ALL_FORMATS)
        }
    }

    private val executorService = Executors.newSingleThreadExecutor()

    private val mImageScanner: ImageScanner
    private val mHandler: Handler
    private var mCallback: ScanCallback? = null

    //private var isScan = false

    private var allowAnalysis = true
    private var barcode: Image? = null

    init {
        mImageScanner = ImageScanner()
        mImageScanner.setConfig(0, Config.X_DENSITY, 3)
        mImageScanner.setConfig(0, Config.Y_DENSITY, 3)

        mHandler = object : Handler(Looper.getMainLooper()) {

            var bcs = ArrayList<String>()

            override fun handleMessage(msg: Message) {
                if (mCallback != null) {

                    if(bcFormats.contains(BarcodeFormat.getFormat(msg.arg1))) {

                        bcs.add(msg.obj as String)
                        //Log.d("BARCODE", msg.obj as String)
                        //Log.d("BARCODE", " " + msg.arg1)
                        //Log.d("BARCODE", " " + msg.arg2)

                        if (bcs.size >= 3 && bcs[bcs.size - 1] == bcs[bcs.size - 2]) {
                            bcs.clear()

                            mCallback!!.onScanResult(Result(msg.obj as String, BarcodeFormat.getFormat(msg.arg1)))

                            if (format == ScannerFormat.CONTINUE)
                                Handler().postDelayed({
                                onStart()
                            }, delay)

                        } else {
                            onStart()
                        }

                    }else{
                        onStart()
                    }
                }
            }
        }
    }

    private val mAnalysisTask = Runnable {

        val result = mImageScanner.scanImage(barcode)

        var resultStr: String? = null
        var format = -1
        if (result != 0) {
            val symSet = mImageScanner.results
            for (sym in symSet) {
                resultStr = sym.data
                format = sym.type
            }
        }

        if (!TextUtils.isEmpty(resultStr)) {
            val message = mHandler.obtainMessage()
            message.arg1 = format
            message.obj = resultStr
            message.sendToTarget()
        } else {
            allowAnalysis = true
        }
    }

    fun setScanCallback(callback: ScanCallback) {
        this.mCallback = callback
    }

    fun onStop() {
        this.allowAnalysis = false
    }

    fun onStart() {
        this.allowAnalysis = true
    }

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        //Log.d("onPreviewFrame", "allowAnalysis=${this.allowAnalysis}")

        if (allowAnalysis) {
            allowAnalysis = false

            val size = camera.parameters.previewSize

            barcode = Image(size.width, size.height, "Y800")
            barcode!!.data = data

            barcode!!.setCrop(0, 0, size.width, size.height)

            executorService.execute(mAnalysisTask)

        }

    }

    fun isAnalysis(): String {
        return "${this.allowAnalysis}"
    }
}