package com.example.epilepsytestapp.savefiles

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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

fun startRecording(context: Context, videoCapture: VideoCapture<Recorder>): Recording? {
    Log.d("TestScreen", "Démarrage de l'enregistrement")

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
    val outputOptions = FileOutputOptions.Builder(outputFile).build()

    return videoCapture.output.prepareRecording(context, outputOptions)
        .withAudioEnabled()
        .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
            when (recordEvent) {
                is VideoRecordEvent.Start -> {
                    Log.d("TestScreen", "Enregistrement démarré")
                    Toast.makeText(context, "Enregistrement en cours", Toast.LENGTH_SHORT).show()
                }
                is VideoRecordEvent.Finalize -> {
                    Log.d("TestScreen", "Enregistrement terminé : ${outputFile.absolutePath}")
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
