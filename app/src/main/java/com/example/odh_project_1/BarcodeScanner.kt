package com.example.odh_project_1
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class BarcodeScanner(private val activity: AppCompatActivity) {

    fun startScan() {
        val integrator = IntentIntegrator(activity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan a barcode")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): String? {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        return if (result.contents == null) {
            null
        } else {
            result.contents
        }
    }
}