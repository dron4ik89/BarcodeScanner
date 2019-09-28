package andrey.shpilevoy.scanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class BarcodeScanner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    CameraPreview(context, attrs) {

    private val REQUEST_CODE = 777

    fun startPreview() {

        stopScan()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf<String>(Manifest.permission.CAMERA),
                REQUEST_CODE
            )
        } else {
            super.startPreviewBase()
        }
    }

    fun startPreview(format: ScannerFormat) {

        startPreview()
        if(format != ScannerFormat.PREVIEW)
            startScan()
        CameraScanAnalysis.format = format

    }

    fun startPreviewContinue(delay: Long){
        startPreview()
        startScan()

        CameraScanAnalysis.format = ScannerFormat.CONTINUE
        CameraScanAnalysis.delay = delay
    }

    fun startScan() {
        super.startScanBase()
    }

    fun stopScan() {
        super.stopScanBase()
    }

    fun stopPreview() {
        super.stopPreviewBase()
    }

    fun setScanCallback(callback: ScanCallback) {
        super.setScanCallbackBase(callback)
    }

    fun setFormat(format: BarcodeFormat){
        CameraScanAnalysis.bcFormats.clear()
        CameraScanAnalysis.bcFormats.add(format)
    }

    fun setFormats(formats: Array<BarcodeFormat>){
        CameraScanAnalysis.bcFormats.clear()
        CameraScanAnalysis.bcFormats.addAll(formats)
    }

    fun setFormats(formats: ArrayList<BarcodeFormat>){
        CameraScanAnalysis.bcFormats.clear()
        CameraScanAnalysis.bcFormats.addAll(formats)
    }

    fun startFocus(){
        super.startFocusBase()
    }

    fun startFocus(delay: Long){
        super.setFocusDelay(delay)
        super.startFocusBase()
    }

    fun stopFocus(){
        super.stopFocusBase()
    }

    fun lightOn(){
        super.setLight(FlashMode.ON)
    }

    fun lightOff(){
        super.setLight(FlashMode.OFF)
    }

    override fun setLight(mode: FlashMode){
        super.setLight(mode)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("LOG", "requestCode == 777")
            startPreviewBase()
        } else {
            Log.d("LOG", "requestCode != 777")
        }
    }

}