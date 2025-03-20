package com.example.epilepsytestapp.savefiles

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaScannerConnection
import android.util.Log
import java.io.File
import java.nio.ByteBuffer

fun mergeVideos(context: Context, videoPaths: List<String>): String? {
    if (videoPaths.isEmpty()) {
        Log.e("MergeVideo", "❌ Aucune vidéo à fusionner")
        return null
    }

    val videosDirectory = File(context.getExternalFilesDir(null), "EpilepsyTests/Videos")
    if (!videosDirectory.exists()) videosDirectory.mkdirs()

    val outputFile = File(videosDirectory, "final_test_video_${System.currentTimeMillis()}.mp4")
    Log.d("MergeVideo", "📌 Début de la fusion des vidéos, destination : ${outputFile.absolutePath}")

    try {
        val mediaMuxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        val bufferSize = 1024 * 1024 // 1MB buffer
        val buffer = ByteBuffer.allocate(bufferSize)
        val bufferInfo = MediaCodec.BufferInfo()

        var videoTrackIndex = -1
        var audioTrackIndex = -1
        var hasVideoTrack = false
        var hasAudioTrack = false
        var currentPresentationTimeUs = 0L

        // Ajout des pistes vidéo et audio
        for (videoPath in videoPaths) {
            val videoFile = File(videoPath)
            if (!videoFile.exists()) {
                Log.e("MergeVideo", "❌ Le fichier vidéo n'existe pas : $videoPath")
                continue
            }

            val extractor = MediaExtractor()
            extractor.setDataSource(videoPath)

            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("video/") == true && videoTrackIndex == -1) {
                    videoTrackIndex = mediaMuxer.addTrack(format)
                    hasVideoTrack = true
                    Log.d("MergeVideo", "🎬 Piste vidéo ajoutée : $mime")
                } else if (mime?.startsWith("audio/") == true && audioTrackIndex == -1) {
                    audioTrackIndex = mediaMuxer.addTrack(format)
                    hasAudioTrack = true
                    Log.d("MergeVideo", "🎵 Piste audio ajoutée : $mime")
                }
            }
            extractor.release()
        }

        // Démarrage du MediaMuxer après avoir ajouté toutes les pistes
        if (hasVideoTrack || hasAudioTrack) {
            mediaMuxer.start()
        }

        // Lecture des pistes et écriture des échantillons
        for (videoPath in videoPaths) {
            val videoFile = File(videoPath)
            if (!videoFile.exists()) {
                Log.e("MergeVideo", "❌ Le fichier vidéo n'existe pas : $videoPath")
                continue
            }

            val extractor = MediaExtractor()
            extractor.setDataSource(videoPath)

            for (i in 0 until extractor.trackCount) {
                extractor.selectTrack(i)
                while (true) {
                    bufferInfo.offset = 0
                    bufferInfo.size = extractor.readSampleData(buffer, 0)

                    if (bufferInfo.size < 0) {
                        Log.d("MergeVideo", "✅ Fin de la lecture du fichier : $videoPath")
                        break
                    }

                    bufferInfo.presentationTimeUs = currentPresentationTimeUs + extractor.sampleTime
                    bufferInfo.flags = extractor.sampleFlags
                    mediaMuxer.writeSampleData(if (extractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME)?.startsWith("video/") == true) videoTrackIndex else audioTrackIndex, buffer, bufferInfo)

                    extractor.advance()
                }
                extractor.unselectTrack(i)
            }
            currentPresentationTimeUs += bufferInfo.presentationTimeUs + bufferInfo.size
            extractor.release()
        }

        mediaMuxer.stop()
        mediaMuxer.release()

        Log.d("MergeVideo", "✅ Fusion des vidéos réussie : ${outputFile.absolutePath}")

        // 📌 Scanner le fichier pour qu'il soit visible dans les fichiers multimédias
        MediaScannerConnection.scanFile(context, arrayOf(outputFile.absolutePath), arrayOf("video/mp4")) { path, uri ->
            Log.d("MergeVideo", "📂 Vidéo ajoutée aux fichiers multimédias : $path | URI : $uri")
        }

        return outputFile.absolutePath
    } catch (e: Exception) {
        Log.e("MergeVideo", "❌ Erreur lors de la fusion des vidéos", e)
        return null
    }
}