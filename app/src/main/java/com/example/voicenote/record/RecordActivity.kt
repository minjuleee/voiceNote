package com.example.voicenote.record

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voicenote.R
import com.example.voicenote.home.HomeActivity
import com.example.voicenote.detail.DetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import java.io.IOException

class RecordActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private lateinit var textLive: TextView
    private lateinit var buttonRecord: FloatingActionButton
    private var isListening = false

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
            if (!isListening) {
                startListening()
                buttonRecord.setImageResource(R.drawable.ic_stop)
            } else {
                stopListening()
                buttonRecord.setImageResource(R.drawable.microphone_solid)
            }
        }

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)); true }
                R.id.nav_detail -> { startActivity(Intent(this, DetailActivity::class.java)); true }
                else -> false
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
        } else {
            Toast.makeText(this, "음성 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
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
            override fun onReadyForSpeech(params: Bundle?) { textLive.text = "🎙 말하세요..." }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let {
                    val resultText = it.joinToString(" ")
                    textLive.text = resultText

                    summarizeText(resultText) { summaryText ->
                        val title = summaryText.split(".").firstOrNull()?.trim() ?: "제목 없음"
                        val timestamp = System.currentTimeMillis()

                        val data = hashMapOf(
                            "text" to resultText,
                            "summary" to summaryText,
                            "title" to title,
                            "timestamp" to timestamp
                        )

                        FirebaseFirestore.getInstance()
                            .collection("voicenote")  // ✅ 여기도 통일
                            .add(data)
                            .addOnSuccessListener {
                                Toast.makeText(this@RecordActivity, "저장 완료", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@RecordActivity, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                stopListening()
                buttonRecord.setImageResource(R.drawable.microphone_solid)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                partial?.let { textLive.text = it.joinToString(" ") }
            }

            override fun onError(error: Int) {
                textLive.text = "에러 발생: $error"
                stopListening()
                buttonRecord.setImageResource(R.drawable.microphone_solid)
            }

            override fun onBeginningOfSpeech() {}
            override fun onEndOfSpeech() {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onRmsChanged(rmsdB: Float) {}
        })
    }

    private fun startListening() { isListening = true; speechRecognizer.startListening(speechIntent) }
    private fun stopListening() { isListening = false; speechRecognizer.stopListening() }

    override fun onDestroy() { super.onDestroy(); speechRecognizer.destroy() }

    private fun summarizeText(inputText: String, callback: (String) -> Unit) {
        // api key 넣어줘야함
        val url = "https://api-inference.huggingface.co/models/facebook/bart-large-cnn"

        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()
        val body = """{ "inputs": "$inputText" }""".toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { runOnUiThread { callback("") } }
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val summaryText = try {
                    val jsonArr = JSONArray(responseBody)
                    jsonArr.getJSONObject(0).getString("summary_text")
                } catch (e: Exception) { "" }
                runOnUiThread { callback(summaryText) }
            }
        })
    }
}
