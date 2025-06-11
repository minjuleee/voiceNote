package com.example.voicenote.detail

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voicenote.R
import com.example.voicenote.home.HomeActivity
import com.example.voicenote.record.RecordActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ImageView

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val titleView = findViewById<TextView>(R.id.textTitle)
        val summaryView = findViewById<TextView>(R.id.textSummary)
        val dateView = findViewById<TextView>(R.id.textDate)
        val rawTextView = findViewById<TextView>(R.id.textLiveTranscript)

        val documentId = intent.getStringExtra("documentId")

        val buttonBack = findViewById<ImageView>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            val intent = Intent(this, com.example.voicenote.home.HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (documentId.isNullOrEmpty()) {
            Toast.makeText(this, "문서 ID가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        FirebaseFirestore.getInstance()
            .collection("voicenote") // ✅ 수정된 부분: 컬렉션 통일
            .document(documentId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val title = doc.getString("title") ?: "(제목 없음)"
                    val summary = doc.getString("summary") ?: "(요약 없음)"
                    val rawText = doc.getString("text") ?: "(원문 없음)"
                    val timestamp = doc.getLong("timestamp") ?: 0L

                    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
                        .format(Date(timestamp))

                    titleView.text = title
                    summaryView.text = summary
                    dateView.text = dateStr
                    rawTextView.text = rawText
                } else {
                    Toast.makeText(this, "데이터가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "데이터 로딩 실패", Toast.LENGTH_SHORT).show()
            }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_detail -> true
                else -> false
            }
        }

        val fabRecord = findViewById<FloatingActionButton>(R.id.fabRecord1)
        fabRecord.setOnClickListener {
            startActivity(Intent(this, RecordActivity::class.java))
        }
    }
}
