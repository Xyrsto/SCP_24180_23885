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
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.ForwardingImageProxy
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.scp_24180_23885.retrofitInterface
import com.google.gson.JsonObject
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    //lateinit var button:Button
    lateinit var imageView:ImageView
    lateinit var colorView: TextView
    lateinit var checkBox: CheckBox
    lateinit var storeColorButton: Button
    private lateinit var cameraView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var storedColor: String
    private lateinit var loginLabel : TextView
    private lateinit var listarLabel : TextView
    private lateinit var colorNameInput : EditText
    private lateinit var currentLoggedUser : String
    private lateinit var currentLoggedUserId : String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        colorView = findViewById<TextView>(R.id.textView2)
        cameraView = findViewById(R.id.cameraView)
        checkBox = findViewById(R.id.checkBox)
        storeColorButton = findViewById(R.id.button)
        loginLabel = findViewById(R.id.loginLabel)
        listarLabel = findViewById(R.id.listarLabel)
        colorNameInput = findViewById(R.id.colorName)

        currentLoggedUser = intent.getStringExtra("loggedUser").toString()
        if(currentLoggedUser !== "null"){
            storeColorButton.isEnabled = true;
            storeColorButton.visibility = View.VISIBLE;

            colorNameInput.isEnabled = true;
            colorNameInput.visibility = View.VISIBLE;

            listarLabel.isEnabled = true;
            listarLabel.visibility = View.VISIBLE

            loginLabel.setText(currentLoggedUser)
             getUserId();
        }else{
            storeColorButton.isEnabled = false;
            storeColorButton.visibility = View.INVISIBLE
            colorNameInput.isEnabled = false;
            colorNameInput.visibility = View.INVISIBLE
            listarLabel.isEnabled = false;
            listarLabel.visibility = View.INVISIBLE
        }


        //bloqueia a orientação, para ficar sempre na vertical
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        storeColorButton.setOnClickListener{
            captureColor()
        }

        loginLabel.setOnClickListener(){
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        listarLabel.setOnClickListener(){
            val intent = Intent(this@MainActivity, ListActivity::class.java)
            intent.putExtra("userId", currentLoggedUserId)
            intent.putExtra("userName", currentLoggedUser)
            startActivity(intent)
        }
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

    private fun updateColorView(color: Int) {
        val hex = String.format("#%06X", 0xFFFFFF and color)

        val handler = Handler()
        handler.postDelayed({
            runOnUiThread {
                colorView.text = hex
                val parsedColor = Color.parseColor(hex)
                checkBox.setBackgroundColor(parsedColor)
            }
        }, 2000) // 1500 milliseconds (1.5 seconds)
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

    private fun captureColor(){
        storedColor = colorView.text.toString()
        //Toast.makeText(this, storedColor + " guardado com sucesso!", Toast.LENGTH_SHORT).show()

        if(colorNameInput.text.toString() == "")
        {
            Toast.makeText(this, "É necessário um nome para a cor", Toast.LENGTH_SHORT).show()

        }
        val api = Retrofit.Builder()
            .baseUrl("https://teste-final-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(retrofitInterface::class.java)

        val body = JsonObject().apply {
            addProperty("name", colorNameInput.text.toString())
            addProperty("description", "é a cor " + storedColor)
            addProperty("color", storedColor)
            addProperty("owner", currentLoggedUserId)
        }

        api.addColor(body).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    Log.e("0000", "success: ${response.body()}")
                    // Handle success - response.body() contains the successful response body
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Cor("+colorNameInput.text.toString()+") adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("0000", "failed: ${response.message()}")
                    // Handle failure - response.message() contains the error message
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Falha ao adicionar cor", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("0000", "error: ${t.message}")
                // Handle failure - t.message contains the failure message
            }
        })
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result -> if(result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            imageView.setImageBitmap(data?.extras?.get("data") as Bitmap)
        }
    }

    fun getUserId(){
        val api = Retrofit.Builder()
            .baseUrl("https://teste-final-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(retrofitInterface::class.java)

        val body = JsonObject().apply {
            addProperty("username", currentLoggedUser)
        }

        api.getId(body).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonObject = response.body()

                    jsonObject?.let{
                        val id = it.getAsJsonPrimitive("id").asString
                        currentLoggedUserId = id;
                    }
                } else {
                    // Handle failure - response.message() contains the error message
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("0000", "error: ${t.message}")
                // Handle failure - t.message contains the failure message
            }
        })
    }
}