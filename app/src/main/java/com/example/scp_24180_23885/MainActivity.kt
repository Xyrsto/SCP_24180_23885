package com.example.scp_24180_23885

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.Executors
import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.util.Size
import android.widget.CheckBox
import android.widget.TextView
import androidx.camera.core.ForwardingImageProxy
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView

class MainActivity : AppCompatActivity() {
    //lateinit var button:Button
    lateinit var imageView:ImageView
    lateinit var colorView: TextView
    lateinit var checkBox: CheckBox
    private lateinit var cameraView: PreviewView
    private lateinit var imageCapture: ImageCapture
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        colorView = findViewById<TextView>(R.id.textView2)
        //imageView = findViewById<ImageView>(R.id.imageView)
        cameraView = findViewById(R.id.cameraView)
        checkBox = findViewById(R.id.checkBox)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        //button.setOnClickListener{
         //   capturePhoto()
       // }
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraView.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder().setTargetResolution(Size(640,480)).build()

            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), { imageProxy ->
                val color = extractColorFromCenter(imageProxy)

                runOnUiThread{
                    updateColorView(color)
                }

                imageProxy.close()
            })

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (exc: Exception) {
                // Handle any exceptions
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     *  @param imageProxy
     *  @return Devolve a cor estraída do centro da câmara
     */
    private fun extractColorFromCenter(imageProxy: ImageProxy):Int{
        val width = imageProxy.width
        val height = imageProxy.height
        val centerX = width / 2
        val centerY = height / 2

        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val yIndex = centerY * imageProxy.planes[0].rowStride + centerX
        val uvIndex = (centerY / 2) * imageProxy.planes[1].rowStride + (centerX / 2) * imageProxy.planes[1].pixelStride

        val y = yBuffer[yIndex].toInt() and 0xFF
        val u = uBuffer[uvIndex].toInt() and 0xFF
        val v = vBuffer[uvIndex].toInt() and 0xFF

        // Convert YUV to RGB
        val r = y + (1.402f * (v - 128)).toInt()
        val g = y - (0.344136f * (u - 128) + 0.714136f * (v - 128)).toInt()
        val b = y + (1.772f * (u - 128)).toInt()

        return Color.rgb(
            r.coerceIn(0, 255),
            g.coerceIn(0, 255),
            b.coerceIn(0, 255)
        )
    }

    private fun updateColorView(color: Int){
        val hex = String.format("#%06X", 0xFFFFFF and color)

        runOnUiThread{
            colorView.text = hex
            val parsedColor = Color.parseColor(hex)
            checkBox.setBackgroundColor(parsedColor)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    // Create a directory to store captured images
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    fun capturePhoto(){
        val outputDirectory = getOutputDirectory()
        val file = File(outputDirectory, "${System.currentTimeMillis()}.jpg")

        val metadata = ImageCapture.Metadata().apply {
            // Add metadata here if desired
        }

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file)
            .setMetadata(metadata)
            .build()

        imageCapture.takePicture(outputFileOptions, Executors.newSingleThreadExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Handle image capture success
                    val savedUri = Uri.fromFile(file)
                    imageView.setImageURI(savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    // Handle image capture error
                }
            })
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result -> if(result.resultCode == Activity.RESULT_OK){
        val data: Intent? = result.data
        imageView.setImageBitmap(data?.extras?.get("data") as Bitmap)
    }
    }
}