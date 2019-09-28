# BarcodeScanner

```
allprojects {
  repositories {
	  maven { url 'https://jitpack.io' }
  }
}
```

``` 
dependencies {
  implementation 'com.github.dron4ik89:BarcodeScanner:0.0.1' 
}
```

## XML
```
<andrey.shpilevoy.scanner.BarcodeScanner
       android:id="@+id/camera_view"
       android:layout_width="match_parent"
       android:layout_height="match_parent"/>
```

## Kotlin Activity
```
override fun onStart() {
    super.onStart()
    camera_view.startPreview()
}

override fun onDestroy() {
    camera_view.stopPreview()
    super.onDestroy()
}
```

### The view automatically requests the rights, they need to be processed
```
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        camera_view.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
}
```
 
## Exposure methods
### Start preview
```
  startPreview() // Standart
  startPreview(format: ScannerFormat) // PREVIEW == startPreview() || SINGLE Scans once || CONTINUE Scans without a break
  startPreviewContinue(delay: Long) // Scans without interruption with a delay
```

### Stop preview
```
  stopPreview()
```

### Start scan
```
  startScan()
```

### Stop scan
```
  stopScan()
```

### Callback
```
  setScanCallback(callback: ScanCallback)
```

### Set format barcode // By default, all formats
```
  setFormat(format: BarcodeFormat) // will scan only the specified format
  setFormats(formats: Array<BarcodeFormat>) // will scan all given formats
```

