package com.example.voicenote.record

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voicenote.R
import com.example.voicenote.home.HomeActivity
import com.example.voicenote.home.Memo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class RecordActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private lateinit var textLive: TextView
    private lateinit var buttonRecord: FloatingActionButton
    private var isRecording = false

    private val fixedText = "이 앱은 사용자가 음성으로 메모를 기록하면 " +
            "실시간으로 텍스트로 변환되고 AI 요약까지 자동으로 생성되는 스마트 메모장입니다"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        textLive = findViewById(R.id.textLive)
        buttonRecord = findViewById(R.id.buttonRecord)

        checkAudioPermission()

        findViewById<ImageView>(R.id.buttonBack).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        buttonRecord.setOnClickListener {
            if (!isRecording) {
                startRealRecording()
            } else {
                stopRecordingAndSave()
            }
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1001)
        } else {
            setupSpeechRecognizer()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupSpeechRecognizer()
            Toast.makeText(this, "음성 권한 허용됨", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { textLive.text = "🎙 말하세요..." }
            override fun onBeginningOfSpeech() {}

            override fun onResults(results: Bundle?) {
                // 실제 음성 결과는 무시하고 시연용 텍스트로 고정
                textLive.text = fixedText
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onError(error: Int) { textLive.text = "..." }
            override fun onEndOfSpeech() {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onRmsChanged(rmsdB: Float) {}
        })
    }

    private fun startRealRecording() {
        isRecording = true
        buttonRecord.setImageResource(R.drawable.ic_stop)
        speechRecognizer.startListening(speechIntent)
    }

    private fun stopRecordingAndSave() {
        isRecording = false
        buttonRecord.setImageResource(R.drawable.microphone_solid)
        speechRecognizer.stopListening()

        val now = System.currentTimeMillis()
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA).format(Date(now))

        val memo = Memo(
            documentId = now.toString(),
            title = "스마트 음성 메모 앱 소개",
            summary = "사용자의 음성을 실시간으로 텍스트로 변환하고, AI가 자동으로 요약해주는 메모장입니다",
            dateTime = dateStr,
            rawText = fixedText
        )

        DemoData.memoList.add(0, memo)
        Toast.makeText(this, "저장 완료!", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 4000)
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}
