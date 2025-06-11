package com.example.voicenote.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicenote.R
import com.example.voicenote.detail.DetailActivity
import com.example.voicenote.record.RecordActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: MemoAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private var memoList: MutableList<Memo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMemos)
        adapter = MemoAdapter(memoList) { selectedMemo ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("documentId", selectedMemo.documentId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadMemosFromFirebase()

        val editTextSearch = findViewById<EditText>(R.id.editTextSearch)
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
        })

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

        val fabRecord = findViewById<FloatingActionButton>(R.id.fabRecord)
        fabRecord.setOnClickListener {
            startActivity(Intent(this, RecordActivity::class.java))
        }
    }

    private fun loadMemosFromFirebase() {
        firestore.collection("voicenote")
            .get()
            .addOnSuccessListener { querySnapshot ->
                memoList.clear()
                for (doc in querySnapshot) {
                    try {
                        val timestamp = doc.getLong("timestamp") ?: 0L
                        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA).format(Date(timestamp))

                        val memo = Memo(
                            documentId = doc.id,
                            title = doc.getString("title") ?: "제목 없음",
                            summary = doc.getString("summary") ?: "",
                            dateTime = dateStr,
                            rawText = doc.getString("text") ?: ""
                        )

                        memoList.add(memo)
                    } catch (e: Exception) {
                        Log.e("HomeActivity", "문서 파싱 실패", e)
                    }
                }
                adapter.updateList(memoList)
            }
            .addOnFailureListener { e ->
                Log.e("HomeActivity", "Firestore 로드 실패", e)
            }
    }
}
