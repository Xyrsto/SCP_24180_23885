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

/**
 * Classe principal da aplicação (MainActivity).
 *
 * Esta classe é responsável pela lógica principal da aplicação SCP, incluindo a captura de cores
 * pela câmara, o armazenamento de cores e a navegação entre diferentes atividades.
 *
 * @property colorView Componente de exibição para a representação hexadecimal da cor capturada.
 * @property checkBox Caixa de seleção para exibir a cor capturada.
 * @property storeColorButton Botão para capturar e armazenar a cor.
 * @property cameraView Componente para visualização da câmara.
 * @property imageCapture Objeto responsável pela captura de imagens.
 * @property storedColor Cor capturada e armazenada.
 * @property loginLabel Rótulo para exibir o nome do utilizador logado.
 * @property listarLabel Rótulo para navegar até a atividade de listagem.
 * @property scpLabel Rótulo para navegar até a atividade de informações.
 * @property colorNameInput Entrada de texto para o nome da cor.
 * @property currentLoggedUser Nome do utilizador atualmente logado.
 * @property currentLoggedUserId ID do utilizador atualmente logado.
 */
class MainActivity : AppCompatActivity() {
    lateinit var colorView: TextView
    lateinit var checkBox: CheckBox
    lateinit var storeColorButton: Button
    private lateinit var cameraView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var storedColor: String
    private lateinit var loginLabel: TextView
    private lateinit var listarLabel: TextView
    private lateinit var scpLabel: TextView
    private lateinit var colorNameInput: EditText
    private lateinit var currentLoggedUser: String
    private lateinit var currentLoggedUserId: String

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
        scpLabel = findViewById(R.id.scpLabel)

        currentLoggedUser = intent.getStringExtra("loggedUser").toString()
        if (currentLoggedUser !== "null") {
            storeColorButton.isEnabled = true;
            storeColorButton.visibility = View.VISIBLE;

            colorNameInput.isEnabled = true;
            colorNameInput.visibility = View.VISIBLE;

            listarLabel.isEnabled = true;
            listarLabel.visibility = View.VISIBLE

            loginLabel.setText(currentLoggedUser)
            getUserId();
        } else {
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

        storeColorButton.setOnClickListener {
            captureColor()
        }

        loginLabel.setOnClickListener() {
            if (currentLoggedUser === "null") {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@MainActivity, UserProfile::class.java)
                intent.putExtra("userId", currentLoggedUserId)
                intent.putExtra("userName", currentLoggedUser)
                startActivity(intent)
            }
        }

        listarLabel.setOnClickListener() {
            val intent = Intent(this@MainActivity, ListActivity::class.java)
            intent.putExtra("userId", currentLoggedUserId)
            intent.putExtra("userName", currentLoggedUser)
            startActivity(intent)
        }

        scpLabel.setOnClickListener() {
            val intent = Intent(this@MainActivity, InfoActivity::class.java)
            intent.putExtra("userId", currentLoggedUserId)
            intent.putExtra("userName", currentLoggedUser)
            startActivity(intent)
        }
    }

    /**
     * Inicializa e configura a câmara para captura de cor.
     *
     * Este método utiliza o [ProcessCameraProvider] para configurar a câmara, definir um
     * [Preview] e um [ImageAnalysis] para análise da cor capturada.
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraView.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder().setTargetResolution(Size(640, 480)).build()

            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), { imageProxy ->
                val color = extractColorFromCenter(imageProxy)

                runOnUiThread {
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
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Extrai a cor do centro da imagem capturada.
     *
     * Este método realiza a conversão da representação YUV para RGB e retorna a cor resultante.
     *
     * @param imageProxy Objeto [ImageProxy] contendo a imagem capturada.
     * @return A cor RGB extraída do centro da imagem.
     */
    private fun extractColorFromCenter(imageProxy: ImageProxy): Int {
        val width = imageProxy.width
        val height = imageProxy.height
        val centerX = width / 2
        val centerY = height / 2

        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val yIndex = centerY * imageProxy.planes[0].rowStride + centerX
        val uvIndex =
            (centerY / 2) * imageProxy.planes[1].rowStride + (centerX / 2) * imageProxy.planes[1].pixelStride

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

    /**
     * Atualiza a visualização da cor capturada na interface do utilizador.
     *
     * Este método converte a cor para representação hexadecimal, exibe-a em um [TextView]
     * e define a cor de fundo de uma [CheckBox].
     *
     * @param color A cor a ser exibida.
     */
    private fun updateColorView(color: Int) {
        val hex = String.format("#%06X", 0xFFFFFF and color)

        val handler = Handler()
        handler.postDelayed({
            runOnUiThread {
                colorView.text = hex
                val parsedColor = Color.parseColor(hex)
                checkBox.setBackgroundColor(parsedColor)
            }
        }, 2000)
    }

    /**
     * Verifica se todas as permissões necessárias foram concedidas.
     *
     * @return `true` se todas as permissões foram concedidas, `false` caso contrário.
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    /**
     * Captura a cor atualmente exibida e a armazena no servidor.
     *
     * Este método utiliza a Retrofit para realizar uma chamada à API e armazenar a cor capturada.
     */
    private fun captureColor() {
        storedColor = colorView.text.toString()

        if (colorNameInput.text.toString() == "") {
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
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "Cor(" + colorNameInput.text.toString() + ") adicionada com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("0000", "failed: ${response.message()}")
                    // Handle failure - response.message() contains the error message
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "Falha ao adicionar cor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("0000", "error: ${t.message}")
            }
        })
    }

    /**
     * Obtém o ID do utilizador atualmente logado.
     *
     * Este método utiliza a Retrofit para realizar uma chamada à API e obter o ID do utilizador
     * com base no nome do utilizador atualmente logado.
     */
    fun getUserId() {
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

                    jsonObject?.let {
                        val id = it.getAsJsonPrimitive("id").asString
                        currentLoggedUserId = id;
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("0000", "error: ${t.message}")
            }
        })
    }
}