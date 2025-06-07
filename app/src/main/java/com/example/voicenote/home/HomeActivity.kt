package com.example.voicenote.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.voicenote.R
import com.example.voicenote.detail.DetailActivity
import com.example.voicenote.record.RecordActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 하단 탭바
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> true
                R.id.nav_detail -> {
                    startActivity(Intent(this, DetailActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // 중앙 녹음 버튼
        val fabRecord = findViewById<FloatingActionButton>(R.id.fabRecord)
        fabRecord.setOnClickListener {
            startActivity(Intent(this, RecordActivity::class.java))
        }
    }
}
