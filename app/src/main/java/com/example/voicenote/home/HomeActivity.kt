package com.example.voicenote.home

import MemoAdapter
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicenote.R
import com.example.voicenote.detail.DetailActivity

import com.example.voicenote.record.RecordActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton



class HomeActivity : AppCompatActivity() {
    private lateinit var adapter: MemoAdapter // ✅ 전역으로 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 1. RecyclerView 세팅
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMemos)
        val dummyData = listOf(
            Memo("안드로이드 프로그래밍 강의", "AI 요약 - 어쩌구 저쩌구", "2025.05.17 토 오후 06:52 18초", rawText = "ViewModel과 LiveData 설명 실시간 녹음 내용입니다."),
            Memo("iOS 개발 기초", "AI 요약 - SwiftUI로 만드는 앱", "2025.05.16 금 오전 09:30 10초", rawText = "SwiftUI의 View 구조, NavigationStack, State 등을 설명한 음성 내용입니다.")
        )

        adapter = MemoAdapter(dummyData) { selectedMemo ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("memo", selectedMemo) // Memo 객체 전달
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 2. 검색창 이벤트
        val editTextSearch = findViewById<EditText>(R.id.editTextSearch)
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString()) // ✅ 이제 정상 동작
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 3. 하단 탭바
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

        // 4. 중앙 녹음 버튼
        val fabRecord = findViewById<FloatingActionButton>(R.id.fabRecord)
        fabRecord.setOnClickListener {
            startActivity(Intent(this, RecordActivity::class.java))
        }
    }
}


