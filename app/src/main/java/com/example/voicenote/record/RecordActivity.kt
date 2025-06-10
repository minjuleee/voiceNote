package com.example.voicenote.record

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voicenote.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RecordActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private lateinit var textLive: TextView
    private lateinit var buttonRecord: FloatingActionButton

    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        // 뷰 바인딩
        textLive = findViewById(R.id.textLive)
        buttonRecord = findViewById(R.id.buttonRecord)

        // 퍼미션 확인 및 음성 인식 초기화
        checkAudioPermission()

        // 녹음 버튼 클릭
        buttonRecord.setOnClickListener {
            if (!isListening) {
                startListening()
                buttonRecord.setImageResource(R.drawable.ic_stop)
            } else {
                stopListening()
                buttonRecord.setImageResource(R.drawable.microphone_solid)
            }
        }

        // 하단 네비게이션 바
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, com.example.voicenote.home.HomeActivity::class.java))
                    true
                }
                R.id.nav_detail -> {
                    startActivity(Intent(this, com.example.voicenote.detail.DetailActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1001
            )
        } else {
            setupSpeechRecognizer()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupSpeechRecognizer()
            } else {
                Toast.makeText(this, "음성 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "음성 인식 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("STT", "onReadyForSpeech 호출됨")
                textLive.text = "🎙 말하세요..."
            }

            override fun onResults(results: Bundle?) {
                Log.d("STT", "onResults 호출됨")
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let {
                    val resultText = it.joinToString(" ")
                    textLive.text = resultText
                    Toast.makeText(this@RecordActivity, "결과: $resultText", Toast.LENGTH_SHORT).show()

                    // Firestore 저장
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
                    val data = hashMapOf(
                        "userId" to userId,
                        "text" to resultText,
                        "timestamp" to System.currentTimeMillis()
                    )

                    FirebaseFirestore.getInstance()
                        .collection("voice_notes")
                        .add(data)
                        .addOnSuccessListener {
                            Toast.makeText(this@RecordActivity, "STT 결과 저장 완료", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@RecordActivity, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }

                stopListening()
                buttonRecord.setImageResource(R.drawable.microphone_solid)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("STT", "onPartialResults 호출됨")
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                partial?.let {
                    textLive.text = it.joinToString(" ")
                }
            }

            override fun onError(error: Int) {
                Log.d("STT", "onError 발생: $error")
                textLive.text = "에러 발생: $error"
                stopListening()
                buttonRecord.setImageResource(R.drawable.microphone_solid)
            }

            override fun onBeginningOfSpeech() {
                Log.d("STT", "onBeginningOfSpeech 호출됨")
            }

            override fun onEndOfSpeech() {
                Log.d("STT", "onEndOfSpeech 호출됨")
            }

            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onRmsChanged(rmsdB: Float) {}
        })
    }

    private fun startListening() {
        isListening = true
        speechRecognizer.startListening(speechIntent)
        Log.d("STT", "startListening 호출됨")
    }

    private fun stopListening() {
        isListening = false
        speechRecognizer.stopListening()
        Log.d("STT", "stopListening 호출됨")
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}
