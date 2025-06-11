package com.example.voicenote.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicenote.R
import com.example.voicenote.detail.DetailActivity
import com.example.voicenote.record.RecordActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: MemoAdapter
    private var memoList: MutableList<Memo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        memoList = DemoData.memoList

        adapter = MemoAdapter(memoList) { selectedMemo ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("documentId", selectedMemo.documentId)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMemos)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 최초 시연 데이터
        if (DemoData.memoList.isEmpty()) {
            loadDemoMemos()
        }

        // 검색창
        val editTextSearch = findViewById<EditText>(R.id.editTextSearch)
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
        })

        // 하단바
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> true
                R.id.nav_detail -> true
                else -> false
            }
        }

        val fabRecord = findViewById<FloatingActionButton>(R.id.fabRecord)
        fabRecord.setOnClickListener {
            startActivity(Intent(this, RecordActivity::class.java))
        }

        // ✅ 드래그 삭제 기능 추가
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                DemoData.memoList.removeAt(position)
                adapter.updateList(DemoData.memoList)
                Toast.makeText(this@HomeActivity, "메모 삭제됨", Toast.LENGTH_SHORT).show()
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun loadDemoMemos() {
        val sampleData = listOf(
            Memo(
                documentId = "1",
                title = "시연용 테스트",
                summary = "시연용 간단 요약입니다.",
                dateTime = "2025-06-11 17:00",
                rawText = "이것은 시연용 데이터입니다. 실제 녹음 내용이 여기에 들어갈 수 있습니다."
            )
        )
        DemoData.memoList.addAll(sampleData)
        adapter.updateList(DemoData.memoList)
    }
}
