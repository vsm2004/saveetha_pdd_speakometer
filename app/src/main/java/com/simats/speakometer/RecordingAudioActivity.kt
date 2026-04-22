package com.simats.speakometer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.util.Locale

class RecordingAudioActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var tvTitle: TextView
    private var isRecording = false
    private var secondsElapsed = 0
    private val handler = Handler(Looper.getMainLooper())

    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String = ""

    private val timerRunnable = object : Runnable {
        override fun run() {
            secondsElapsed++
            val minutes = secondsElapsed / 60
            val seconds = secondsElapsed % 60
            tvTimer.text = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording_audio)

        tvTitle = findViewById(R.id.tv_record_title)
        tvTimer = findViewById(R.id.tv_timer)
        tvSubtitle = findViewById(R.id.tv_record_subtitle)
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val micBg = findViewById<View>(R.id.view_mic_bg)

        // Prepare the output file path
        audioFilePath = "${cacheDir.absolutePath}/recorded_audio.mp4"

        // PERSONALIZATION LOGIC: Check if we came from the "Tips" screen
        val isPracticeMode = intent.getBooleanExtra("IS_PRACTICE_MODE", false)
        if (isPracticeMode) {
            val topic = intent.getStringExtra("PRACTICE_TOPIC") ?: "Exercise"
            tvTitle.text = "Practice: $topic"
            tvSubtitle.text = "Tap to start exercise"
        }

        btnBack.setOnClickListener {
            stopRecordingSilently()
            finish()
        }

        micBg.setOnClickListener {
            if (!isRecording) {
                // Check permission before recording
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.RECORD_AUDIO), 200)
                } else {
                    startRecording()
                }
            } else {
                stopRecordingAndProceed()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRecording()
        } else {
            Toast.makeText(this, "Microphone permission is required to record audio", Toast.LENGTH_LONG).show()
        }
    }

    private fun startRecording() {
        try {
            // Delete old recording if exists
            File(audioFilePath).let { if (it.exists()) it.delete() }

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(16000)
                setAudioEncodingBitRate(128000)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }

            isRecording = true
            secondsElapsed = 0
            tvSubtitle.text = getString(R.string.recording_in_progress)
            handler.post(timerRunnable)

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to start recording: ${e.message}", Toast.LENGTH_LONG).show()
            mediaRecorder?.release()
            mediaRecorder = null
        }
    }

    private fun stopRecordingAndProceed() {
        isRecording = false
        handler.removeCallbacks(timerRunnable)

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            // Ignore stop errors (e.g. if recording was too short)
        }
        mediaRecorder = null

        if (secondsElapsed < 5) {
            // Too Short
            val intent = Intent(this, TooShortAudioActivity::class.java)
            startActivity(intent)
        } else {
            // Proceed to Analysis with the real audio file
            val intent = Intent(this, RecordedAudioAnalysisActivity::class.java)
            intent.putExtra("AUDIO_FILE_PATH", audioFilePath)
            
            // Pass practice flags forward
            val isPractice = getIntent().getBooleanExtra("IS_PRACTICE_MODE", false)
            val topic = getIntent().getStringExtra("PRACTICE_TOPIC")
            intent.putExtra("IS_PRACTICE_MODE", isPractice)
            intent.putExtra("PRACTICE_TOPIC", topic)
            
            startActivity(intent)
        }
        finish()
    }

    private fun stopRecordingSilently() {
        if (isRecording) {
            try {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
            } catch (_: Exception) { }
            mediaRecorder = null
            isRecording = false
            handler.removeCallbacks(timerRunnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecordingSilently()
        handler.removeCallbacks(timerRunnable)
    }
}