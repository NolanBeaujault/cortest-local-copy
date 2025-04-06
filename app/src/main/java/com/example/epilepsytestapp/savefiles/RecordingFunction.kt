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

fun startRecording(
    context: Context,
    videoCapture: VideoCapture<Recorder>,
    recordingState: MutableState<Recording?>,
    videoFilePathState: MutableState<String?>,
    recordedVideos: MutableList<String>
): Recording? {
    Log.d("TestScreen", "Démarrage de l'enregistrement")

    // Vérification des permissions
    val hasPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    if (!hasPermissions) {
        Log.e("TestScreen", "Permissions insuffisantes pour enregistrer")
        Toast.makeText(context, "Permissions non accordées", Toast.LENGTH_LONG).show()
        return null
    }

    // Préparation du répertoire de stockage
    val videosDirectory = File(context.getExternalFilesDir(null), "EpilepsyTests/Videos")
    if (!videosDirectory.exists()) {
        if (!videosDirectory.mkdirs()) {
            Log.e("TestScreen", "❌ Erreur lors de la création du répertoire des vidéos.")
            Toast.makeText(context, "Erreur lors de la création du répertoire", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    // Création d'un fichier pour la vidéo
    val outputFile = File(videosDirectory, "test_screen_record_${System.currentTimeMillis()}.mp4")

    // Vérification immédiate de l'existence du fichier (au cas où la création échouerait)
    if (!outputFile.exists()) {
        try {
            if (outputFile.createNewFile()) {
                Log.d("TestScreen", "Fichier vidéo créé avec succès : ${outputFile.absolutePath}")
            } else {
                Log.e("TestScreen", "❌ Impossible de créer le fichier vidéo.")
                Toast.makeText(context, "Erreur lors de la création du fichier vidéo", Toast.LENGTH_SHORT).show()
                return null
            }
        } catch (e: Exception) {
            Log.e("TestScreen", "Erreur lors de la création du fichier vidéo", e)
            Toast.makeText(context, "Erreur lors de la création du fichier vidéo", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    val outputOptions = FileOutputOptions.Builder(outputFile).build()

    // Si un enregistrement est déjà en cours, arrêtez-le d'abord
    recordingState.value?.stop()

    // Démarrage de l'enregistrement vidéo
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
                    // Ajouter le chemin du fichier vidéo à recordedVideos si ce n'est pas déjà fait
                    if (!recordedVideos.contains(outputFile.absolutePath)) {
                        recordedVideos.add(outputFile.absolutePath)
                        Log.d("TestScreen", "🎬 Vidéo ajoutée à la liste : ${outputFile.absolutePath}")
                    }

                }
            }
        }.also {
            recordingState.value = it
            videoFilePathState.value = outputFile.absolutePath // Juste enregistrer le chemin ici
        }
}

fun stopRecording(
    context: Context,
    recordingState: MutableState<Recording?>,
    videoFilePathState: MutableState<String?>,
    recordedVideos: MutableList<String>
): String? {
    return try {
        // Vérification de l'enregistrement actif
        recordingState.value?.stop()
        recordingState.value = null
        Log.d("TestScreen", "Enregistrement arrêté avec succès")
        Toast.makeText(context, "Enregistrement arrêté", Toast.LENGTH_SHORT).show()

        // Récupération du chemin du fichier vidéo
        val videoFilePath = videoFilePathState.value

        // Vérification si le fichier existe après l'arrêt de l'enregistrement
        if (!videoFilePath.isNullOrEmpty()) {
            val videoFile = File(videoFilePath)

            // Vérification immédiate de l'existence du fichier
            if (videoFile.exists()) {
                // Ajouter le chemin du fichier vidéo à recordedVideos si ce n'est pas déjà fait
                if (!recordedVideos.contains(videoFilePath)) {
                    recordedVideos.add(videoFilePath)
                    Log.d("TestScreen", "🎬 Vidéo ajoutée à la liste : ${videoFilePath}")
                }
                return videoFilePath
            } else {
                Log.e("TestScreen", "❌ Le fichier vidéo n'existe pas après l'enregistrement : $videoFilePath")
                Toast.makeText(context, "Erreur : Le fichier vidéo n'existe pas", Toast.LENGTH_SHORT).show()
                return null
            }
        } else {
            Log.e("TestScreen", "❌ Chemin du fichier vidéo non disponible")
            Toast.makeText(context, "Erreur : Chemin du fichier vidéo non disponible", Toast.LENGTH_SHORT).show()
            return null
        }
    } catch (e: Exception) {
        Log.e("TestScreen", "Erreur lors de l'arrêt de l'enregistrement", e)
        Toast.makeText(context, "Erreur lors de l'arrêt de l'enregistrement", Toast.LENGTH_SHORT).show()
        return null
    }
}
