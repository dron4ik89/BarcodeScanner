package andrey.shpilevoy.qrexample

import andrey.shpilevoy.scanner.Result
import andrey.shpilevoy.scanner.ScanCallback
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        camera_view.setScanCallback(object : ScanCallback {
            override fun onScanResult(result: Result) {

                camera_view.stopScan()

                Handler().postDelayed({
                    camera_view.startScan()
                }, 1000)

                content_view.text = " ${result.format.name} \n\n ${result.content} "
            }

        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        camera_view.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()

        camera_view.startPreview()
        camera_view.startScan()
    }

    override fun onPause() {

        camera_view.stopPreview()

        super.onPause()
    }
}
