package com.example.voicenote.detail

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.voicenote.R
import com.example.voicenote.home.HomeActivity
import com.example.voicenote.home.Memo

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val titleView = findViewById<TextView>(R.id.textTitle)
        val summaryView = findViewById<TextView>(R.id.textSummary)
        val dateView = findViewById<TextView>(R.id.textDate)
        val rawTextView = findViewById<TextView>(R.id.textLiveTranscript)

        val documentId = intent.getStringExtra("documentId")
        val memo = DemoData.memoList.find { it.documentId == documentId }

        if (memo != null) {
            titleView.text = memo.title
            summaryView.text = memo.summary
            dateView.text = memo.dateTime
            rawTextView.text = memo.rawText
        } else {
            titleView.text = "(데이터 없음)"
        }

        findViewById<ImageView>(R.id.buttonBack).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
