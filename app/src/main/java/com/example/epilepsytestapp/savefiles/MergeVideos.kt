package com.example.epilepsytestapp.savefiles

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaScannerConnection
import android.net.Uri
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
        val extractor = MediaExtractor()
        val bufferInfo = MediaCodec.BufferInfo()

        var trackIndex = -1
        var hasVideoTrack = false

        for (videoPath in videoPaths) {
            extractor.setDataSource(videoPath)
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("video/") == true) {
                    if (!hasVideoTrack) {
                        trackIndex = mediaMuxer.addTrack(format)
                        mediaMuxer.start()
                        hasVideoTrack = true
                        Log.d("MergeVideo", "🎬 Piste vidéo ajoutée : $mime")
                    }
                    extractor.selectTrack(i)
                    break
                }
            }
        }

        if (!hasVideoTrack) {
            Log.e("MergeVideo", "❌ Aucune piste vidéo valide trouvée")
            return null
        }

        for (videoPath in videoPaths) {
            Log.d("MergeVideo", "🔄 Traitement de la vidéo : $videoPath")
            extractor.setDataSource(videoPath)
            extractor.selectTrack(trackIndex)

            while (true) {
                bufferInfo.offset = 0
                bufferInfo.size = extractor.readSampleData(buffer, 0)

                if (bufferInfo.size < 0) {
                    Log.d("MergeVideo", "✅ Fin de la lecture du fichier : $videoPath")
                    break
                }

                bufferInfo.presentationTimeUs = extractor.sampleTime
                bufferInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME
                mediaMuxer.writeSampleData(trackIndex, buffer, bufferInfo)

                extractor.advance()
            }
        }

        mediaMuxer.stop()
        mediaMuxer.release()
        extractor.release()

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
