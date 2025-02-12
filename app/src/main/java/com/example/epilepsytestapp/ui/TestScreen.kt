package com.example.epilepsytestapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.util.Size
import android.view.Surface
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@Composable
fun TestScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val hasPermissions = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    if (!hasPermissions) {
        Toast.makeText(context, "Permissions caméra et microphone requises", Toast.LENGTH_LONG).show()
        navController.navigate("home")
        return
    }

    var currentInstructionIndex by remember { mutableIntStateOf(0) }
    val instructions = listOf(
        "Regardez l'écran pendant 10 secondes.",
        "Fermez les yeux et détendez-vous.",
        "Suivez les instructions affichées à l'écran.",
        "Levez votre bras droit.",
        "Levez votre bras gauche."
    )
    val currentInstruction = instructions.getOrNull(currentInstructionIndex)

    var isRecording by remember { mutableStateOf(false) }
    val videoCapture = remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    val currentRecording = remember { mutableStateOf<Recording?>(null) }

    // Début de l'enregistrement lors du chargement de l'écran
    LaunchedEffect(videoCapture.value) {
        videoCapture.value?.let {
            if (!isRecording) {
                currentRecording.value = startRecording(context, it)
                isRecording = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            context = context,
            lifecycleOwner = lifecycleOwner,
            modifier = Modifier.fillMaxSize(),
            videoCapture = videoCapture
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentInstruction != null) {
                Text(
                    text = currentInstruction,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ImageClickable(
                imageResId = R.mipmap.ic_close_foreground,
                contentDescription = "Arrêter le test",
                onClick = {
                    // Arrêter l'enregistrement avant de quitter l'écran
                    if (isRecording) {
                        stopRecording(context, currentRecording)
                        isRecording = false
                    }
                    navController.navigate("confirmation")
                }
            )

            ImageClickable(
                imageResId = R.mipmap.ic_next_foreground,
                contentDescription = "Instruction suivante",
                onClick = {
                    if (currentInstructionIndex < instructions.size - 1) {
                        currentInstructionIndex++
                    } else {
                        // Arrêter l'enregistrement après les instructions
                        if (isRecording) {
                            stopRecording(context, currentRecording)
                            isRecording = false
                        }
                        navController.navigate("confirmation")
                    }
                }
            )
        }
    }
}

@Composable
fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier,
    videoCapture: MutableState<VideoCapture<Recorder>?>
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .build()

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    preview.setSurfaceProvider(surfaceProvider)

                    val recorder = Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HD))
                        .build()

                    val videoCaptureUseCase = VideoCapture.withOutput(recorder)

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, videoCaptureUseCase
                        )
                        videoCapture.value = videoCaptureUseCase
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erreur lors de l'initialisation de la caméra", Toast.LENGTH_SHORT).show()
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        },
        modifier = modifier
    )
}



class CameraRenderer(
    private val glSurfaceView: GLSurfaceView,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val videoCapture: MutableState<VideoCapture<Recorder>?>
) : GLSurfaceView.Renderer {

    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null
    private var textureId: Int = 0
    private var program: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d("CameraRenderer", "Surface créée")

        // Crée un SurfaceTexture pour capturer le flux de la caméra
        surfaceTexture = SurfaceTexture(0).apply {
            setDefaultBufferSize(1280, 720)  // Assurez-vous que la taille du buffer est adaptée à la caméra
            setOnFrameAvailableListener {
                glSurfaceView.requestRender()  // Demander un rendu quand une nouvelle image est disponible
            }
        }

        // Crée un Surface pour afficher le contenu de la caméra
        surface = Surface(surfaceTexture)

        // Initialiser OpenGL pour afficher la texture
        Log.d("CameraRenderer", "Initialisation OpenGL : création de la texture")
        textureId = generateTexture()

        // Charger et compiler les shaders
        program = createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE)

        // Initialise la caméra en utilisant CameraX
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val preview = Preview.Builder()
                    .setTargetResolution(Size(1280, 720))  // Réglez la résolution de la caméra
                    .build().also { preview ->
                        Log.d("CameraRenderer", "Préparation du SurfaceProvider pour le Preview.")

                        preview.setSurfaceProvider { request ->
                            Log.d("CameraRenderer", "SurfaceRequest reçu : demande de fournir la surface.")

                            // Vérification si la surface est bien initialisée
                            if (surface == null) {
                                Log.e("CameraRenderer", "Surface est null avant de la fournir.")
                                return@setSurfaceProvider
                            }

                            Log.d("CameraRenderer", "Surface valide, fourniture de la surface à CameraX.")

                            // Fournir la surface
                            request.provideSurface(surface!!, ContextCompat.getMainExecutor(context)) { result ->
                                when (result.resultCode) {
                                    SurfaceRequest.Result.RESULT_SURFACE_USED_SUCCESSFULLY -> {
                                        Log.d("CameraRenderer", "Surface fournie avec succès.")
                                    }
                                    else -> {
                                        Log.e("CameraRenderer", "Échec de la fourniture de la surface, code : ${result.resultCode}")
                                    }
                                }
                            }
                        }
                    }

                Log.d("CameraRenderer", "Liaison des use cases Preview et VideoCapture à la caméra.")

                val recorder = Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HD))
                    .build()
                val videoCaptureUseCase = VideoCapture.withOutput(recorder)

                try {
                    // Lier les use cases à la caméra
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, videoCaptureUseCase
                    )
                    Log.d("CameraRenderer", "Use cases liés à la caméra avec succès.")
                } catch (e: Exception) {
                    Log.e("CameraRenderer", "Erreur lors de la liaison des use cases à la caméra", e)
                }

                videoCapture.value = videoCaptureUseCase
                Log.d("CameraRenderer", "Caméra initialisée avec succès")
            } catch (e: Exception) {
                Log.e("CameraRenderer", "Erreur lors de l'initialisation de la caméra", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Mettez à jour la vue pour l'affichage à la bonne taille
        Log.d("CameraRenderer", "Surface modifiée : largeur=$width, hauteur=$height")
        gl?.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Mettez à jour la texture chaque fois qu'une nouvelle image est disponible
        surfaceTexture?.apply {
            updateTexImage()  // Met à jour l'image dans le SurfaceTexture
            Log.d("CameraRenderer", "TexImage mis à jour")
        }

        // Utiliser le programme de shaders
        GLES20.glUseProgram(program)

        // Lier la texture et dessiner le quad
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        drawQuad()

        // Log pour vérifier que la texture est mise à jour correctement
        if (surfaceTexture != null) {
            Log.d("CameraRenderer", "Texture mise à jour et dessinée")
        } else {
            Log.e("CameraRenderer", "SurfaceTexture est nul!")
        }
    }

    private fun generateTexture(): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        return textures[0]
    }

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        return program
    }

    private fun loadShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun drawQuad() {
        val vertices = floatArrayOf(
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f,
            -1f, 1f, 0f, 1f,
            1f, 1f, 1f, 1f
        )
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
            .position(0)

        GLES20.glEnableVertexAttribArray(0)
        GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer)

        vertexBuffer.position(2)
        GLES20.glEnableVertexAttribArray(1)
        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    companion object {
        private const val VERTEX_SHADER_CODE = """
            attribute vec4 position;
            attribute vec2 texCoord;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = position;
                vTexCoord = texCoord;
            }
        """

        private const val FRAGMENT_SHADER_CODE = """
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            uniform samplerExternalOES texture;
            varying vec2 vTexCoord;
            void main() {
                gl_FragColor = texture2D(texture, vTexCoord);
            }
        """
    }
}

fun startRecording(context: Context, videoCapture: VideoCapture<Recorder>): Recording? {
    val hasPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    if (!hasPermissions) {
        Toast.makeText(context, "Permissions non accordées", Toast.LENGTH_LONG).show()
        return null
    }

    val videosDirectory = File(context.getExternalFilesDir(null), "EpilepsyTests/Videos")
    if (!videosDirectory.exists()) videosDirectory.mkdirs()

    val outputFile = File(videosDirectory, "test_screen_record_${System.currentTimeMillis()}.mp4")
    val outputOptions = FileOutputOptions.Builder(outputFile).build()

    return videoCapture.output.prepareRecording(context, outputOptions)
        .withAudioEnabled()
        .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
            when (recordEvent) {
                is VideoRecordEvent.Start -> {
                    Toast.makeText(context, "Enregistrement en cours", Toast.LENGTH_SHORT).show()
                }
                is VideoRecordEvent.Finalize -> {
                    Toast.makeText(context, "Vidéo enregistrée", Toast.LENGTH_SHORT).show()
                }
            }
        }
}

fun stopRecording(context: Context, recording: MutableState<Recording?>) {
    try {
        recording.value?.stop()
        recording.value = null
        Log.d("TestScreen", "Enregistrement arrêté avec succès")
        Toast.makeText(context, "Enregistrement arrêté", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.e("TestScreen", "Erreur lors de l'arrêt de l'enregistrement", e)
        Toast.makeText(context, "Erreur lors de l'arrêt de l'enregistrement", Toast.LENGTH_SHORT).show()
    }
}