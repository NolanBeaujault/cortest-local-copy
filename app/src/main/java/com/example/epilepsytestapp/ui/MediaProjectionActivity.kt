package com.example.epilepsytestapp.ui


import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import com.example.epilepsytestapp.pdf.ScreenRecorder

class MediaProjectionActivity : Activity() {
    private lateinit var screenRecorder: ScreenRecorder
    private lateinit var mediaProjectionManager: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val mediaProjection: MediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data!!)
            val outputPath = "${filesDir}/recorded_test.mp4"
            screenRecorder = ScreenRecorder(this)
            screenRecorder.startRecording(mediaProjection, outputPath)
        } else {
            finish()
        }
    }
}
