package com.example.epilepsytestapp.savefiles

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import java.io.File

fun startRecording(
    context: Context,
    videoCapture: VideoCapture<Recorder>,
    recordingState: MutableState<Recording?>,
    videoFilePathState: MutableState<String?>,
    recordedVideos: MutableList<String>
): Recording? {
    Log.d("TestScreen", "Démarrage de l'enregistrement")

    // Vérifiez que les permissions sont bien accordées
    val hasPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    if (!hasPermissions) {
        Log.e("TestScreen", "Permissions insuffisantes pour enregistrer")
        Toast.makeText(context, "Permissions non accordées", Toast.LENGTH_LONG).show()
        return null
    }

    val videosDirectory = File(context.getExternalFilesDir(null), "EpilepsyTests/Videos")
    if (!videosDirectory.exists()) videosDirectory.mkdirs()

    val outputFile = File(videosDirectory, "test_screen_record_${System.currentTimeMillis()}.mp4")
    val videoFilePath = outputFile.absolutePath
    videoFilePathState.value = videoFilePath

    val outputOptions = FileOutputOptions.Builder(outputFile).build()

    // Si un enregistrement est déjà en cours, arrêtez-le proprement
    recordingState.value?.let {
        Log.d("TestScreen", "Un enregistrement est déjà en cours. Arrêt en cours...")
        stopRecording(context, recordingState, videoFilePathState)
    }

    // Démarrer l'enregistrement
    return try {
        videoCapture.output
            .prepareRecording(context, outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        Log.d("TestScreen", "Enregistrement démarré")
                        Toast.makeText(context, "Enregistrement en cours", Toast.LENGTH_SHORT).show()
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (recordEvent.hasError()) {
                            Log.e("TestScreen", "❌ Erreur à la finalisation de l'enregistrement : ${recordEvent.error}")
                            Toast.makeText(context, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("TestScreen", "✅ Enregistrement terminé avec succès : $videoFilePath")
                            recordedVideos.add(videoFilePath)  // ✅ Ajout après succès
                            Toast.makeText(context, "Vidéo enregistrée", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.also {
                Log.d("TestScreen", "🎥 Démarrage de l'enregistrement effectué.")
                recordingState.value = it
            }
    } catch (e: Exception) {
        Log.e("TestScreen", "Erreur lors du démarrage de l'enregistrement", e)
        null
    }
}

fun stopRecording(
    context: Context,
    recordingState: MutableState<Recording?>,
    videoFilePathState: MutableState<String?>
): String? {
    return try {
        val recording = recordingState.value
        if (recording == null) {
            Log.w("TestScreen", "Aucun enregistrement en cours à arrêter.")
            return null
        }

        // Ajoutez un délai pour donner plus de temps à l'enregistrement pour être finalisé
        recording.stop() // Arrêt immédiat de l'enregistrement
        Log.d("TestScreen", "✅ Enregistrement stoppé. Finalisation en cours...")

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("TestScreen", "✅ Finalisation de l'enregistrement terminée.")
        }, 1000) // Assurez une période de stabilisation avant de finaliser
        recordingState.value = null

        val videoFilePath = videoFilePathState.value
        Log.d("TestScreen", "✅ Enregistrement finalisé avec succès : $videoFilePath")
        Toast.makeText(context, "Enregistrement arrêté", Toast.LENGTH_SHORT).show()

        videoFilePath
    } catch (e: Exception) {
        Log.e("TestScreen", "❌ Erreur lors de l'arrêt de l'enregistrement", e)
        Toast.makeText(context, "Erreur lors de l'arrêt", Toast.LENGTH_SHORT).show()
        null
    }
}