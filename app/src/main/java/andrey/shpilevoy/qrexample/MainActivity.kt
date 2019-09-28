package andrey.shpilevoy.qrexample

import andrey.shpilevoy.scanner.Result
import andrey.shpilevoy.scanner.ScanCallback
import andrey.shpilevoy.scanner.ScannerFormat
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        status_view.text = camera_view.isScan()

        //camera_view.setFormat(BarcodeFormat.EAN13)

        camera_view.setScanCallback(object : ScanCallback {
            override fun onScanResult(result: Result) {
                content_view.text = " ${result.format.name} \n\n ${result.content} "
            }

        })

        start_preview.onClick{
            camera_view.startPreview()
        }

        start_scan.onClick{
            camera_view.startScan()
            status_view.text = camera_view.isScan()
        }

        stop_scan.onClick{
            camera_view.stopScan()
            status_view.text = camera_view.isScan()
        }

        stop_preview.onClick{
            camera_view.stopPreview()
        }



        preview_format.onClick{
            camera_view.startPreview(ScannerFormat.PREVIEW)
        }

        single_format.onClick{
            camera_view.startPreview(ScannerFormat.SINGLE)
        }

        continue_format.onClick{
            camera_view.startPreview(ScannerFormat.CONTINUE)
        }

        continue_format_daley.onClick{
            camera_view.startPreviewContinue(10000)
        }



        focus_stop.onClick{
            camera_view.stopFocus()
        }

        focus_start.onClick{
            camera_view.startFocus()
        }

        focus_delay.onClick{
            camera_view.startFocus(2000L)
        }


        flash_on.onClick{
            camera_view.lightOn()
        }

        flash_off.onClick{
            camera_view.lightOff()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        camera_view.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()

        camera_view.startPreview()
        //camera_view.startScan()
    }

    override fun onDestroy() {
        camera_view.stopPreview()
        super.onDestroy()
    }
}
