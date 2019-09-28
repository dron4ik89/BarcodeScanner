package andrey.shpilevoy.scanner

import com.yanzhenjie.zbar.Symbol

import java.util.ArrayList

class BarcodeFormat(val id: Int, val name: String) {
    companion object {

        val NONE = BarcodeFormat(Symbol.NONE, "NONE")
        val PARTIAL = BarcodeFormat(Symbol.PARTIAL, "PARTIAL")
        val EAN8 = BarcodeFormat(Symbol.EAN8, "EAN8")
        val UPCE = BarcodeFormat(Symbol.UPCE, "UPCE")
        val ISBN10 = BarcodeFormat(Symbol.ISBN10, "ISBN10")
        val UPCA = BarcodeFormat(Symbol.UPCA, "UPCA")
        val EAN13 = BarcodeFormat(Symbol.EAN13, "EAN13")
        val ISBN13 = BarcodeFormat(Symbol.ISBN13, "ISBN13")
        val I25 = BarcodeFormat(Symbol.I25, "I25")
        val DATABAR = BarcodeFormat(Symbol.DATABAR, "DATABAR")
        val DATABAR_EXP = BarcodeFormat(Symbol.DATABAR_EXP, "DATABAR_EXP")
        val CODABAR = BarcodeFormat(Symbol.CODABAR, "CODABAR")
        val CODE39 = BarcodeFormat(Symbol.CODE39, "CODE39")
        val PDF417 = BarcodeFormat(Symbol.PDF417, "PDF417")
        val QRCODE = BarcodeFormat(Symbol.QRCODE, "QRCODE")
        val CODE93 = BarcodeFormat(Symbol.CODE93, "CODE93")
        val CODE128 = BarcodeFormat(Symbol.CODE128, "CODE128")

        val ALL_FORMATS: MutableList<BarcodeFormat> = ArrayList()
        init {
            ALL_FORMATS.add(PARTIAL)
            ALL_FORMATS.add(EAN8)
            ALL_FORMATS.add(UPCE)
            ALL_FORMATS.add(ISBN10)
            ALL_FORMATS.add(UPCA)
            ALL_FORMATS.add(EAN13)
            ALL_FORMATS.add(ISBN13)
            ALL_FORMATS.add(I25)
            ALL_FORMATS.add(DATABAR)
            ALL_FORMATS.add(DATABAR_EXP)
            ALL_FORMATS.add(CODABAR)
            ALL_FORMATS.add(CODE39)
            ALL_FORMATS.add(PDF417)
            ALL_FORMATS.add(QRCODE)
            ALL_FORMATS.add(CODE93)
            ALL_FORMATS.add(CODE128)
        }

        fun getFormat(id: Int): BarcodeFormat {
            for (format in ALL_FORMATS) {
                if (format.id == id) {
                    return format
                }
            }
            return NONE
        }

        fun parse(name: String): BarcodeFormat {
            for (format in ALL_FORMATS) {
                if (format.name.equals(name)) {
                    return format
                }
            }
            return NONE
        }
    }

}
