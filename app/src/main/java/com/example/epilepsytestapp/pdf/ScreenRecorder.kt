package com.example.epilepsytestapp.pdf


import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.view.Surface


class ScreenRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null

    fun startRecording(mediaProjection: MediaProjection, outputPath: String) {
        this.mediaProjection = mediaProjection

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(outputPath)
            setVideoSize(1280, 720)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setVideoEncodingBitRate(512 * 1000)
            setVideoFrameRate(30)
            prepare()
        }

        val surface: Surface = mediaRecorder!!.surface
        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenRecorder",
            1280,
            720,
            context.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            surface,
            null,
            null
        )

        mediaRecorder?.start()
    }

    fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        virtualDisplay?.release()
        mediaProjection?.stop()

        mediaRecorder = null
        virtualDisplay = null
        mediaProjection = null
    }
}
