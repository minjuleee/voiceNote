package com.example.voicenote.record

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.voicenote.R
import com.example.voicenote.detail.DetailActivity

import com.example.voicenote.home.HomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        val buttonRecord = findViewById<ImageButton>(R.id.buttonRecord)

        buttonRecord.setOnClickListener {
            // 녹음 종료 로직이 여기에 들어갈 수 있음

            // 상세 페이지로 이동
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_detail -> {
                    startActivity(Intent(this, DetailActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // 중앙 녹음 버튼
        val fabRecord = findViewById<FloatingActionButton>(R.id.buttonRecord)
        fabRecord.setOnClickListener {
            startActivity(Intent(this, DetailActivity::class.java))
        }
    }
}
