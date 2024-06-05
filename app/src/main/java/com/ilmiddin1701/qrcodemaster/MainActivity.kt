package com.ilmiddin1701.qrcodemaster

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.ilmiddin1701.qrcodemaster.databinding.ActivityMainBinding
import java.io.IOException
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var dspName: String = "IMG_${SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())}"

    private var writePermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var btm: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            edt.addTextChangedListener {
                qrImage.visibility = View.GONE
                btnSave.visibility = View.GONE
                btnGenerate.visibility = View.VISIBLE
            }
            edtFocus.setOnClickListener { showKeyboard(edt) }
            permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                writePermissionGranted = it[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted
            }
            updateOrRequestPermission()
            val anim1 = AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim1)
            val anim2 = AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim2)
            btnGenerate.setOnClickListener {
                if (edt.text.toString().isNotEmpty()) {
                    qrImage.visibility = View.VISIBLE
                    btnGenerate.startAnimation(anim2)
                    btnGenerate.visibility = View.GONE
                    btnSave.startAnimation(anim1)
                    btnSave.visibility = View.VISIBLE
                    qrImage.setImageBitmap(generateQRCode(edt.text.toString()))
                } else {
                    Toast.makeText(this@MainActivity, "Please enter text!", Toast.LENGTH_SHORT).show()
                }
            }
            btnSave.setOnClickListener {
                if (writePermissionGranted) {
                    btnSave.startAnimation(anim2)
                    btnSave.visibility = View.GONE
                    btnGenerate.startAnimation(anim1)
                    btnGenerate.visibility = View.VISIBLE
                    qrImage.visibility = View.GONE
                    edt.text.clear()
                    saveToExternalStorage(dspName, btm)
                    Toast.makeText(this@MainActivity, "Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun generateQRCode(text: String): Bitmap? {
        val qrCodeWriter = QRCodeWriter()
        return try {
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            btm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    btm.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            btm
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    private fun updateOrRequestPermission(){
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        writePermissionGranted = hasWritePermission || minSdk29

        val permissionToRequest = mutableListOf<String>()
        if (!writePermissionGranted){
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionToRequest.isNotEmpty()){
            permissionLauncher.launch(permissionToRequest.toTypedArray())
        }
    }

    private fun showKeyboard(view: View) {
        view.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun saveToExternalStorage(displayName: String, bmp: Bitmap): Boolean{
        val imageCollection = sdk29AdnUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image.jpeg")
        }
        return try {
            contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream!!)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            true
        } catch (e:IOException) {
            e.printStackTrace()
            false
        }
    }
}