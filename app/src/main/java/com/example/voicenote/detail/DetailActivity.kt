package com.example.voicenote.detail

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.voicenote.R
import com.example.voicenote.home.HomeActivity
import com.example.voicenote.home.Memo
import com.example.voicenote.record.RecordActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.os.Handler


class DetailActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val memo = intent.getParcelableExtra<Memo>("memo")

        val titleView = findViewById<TextView>(R.id.textTitle)
        val summaryView = findViewById<TextView>(R.id.textSummary)
        val dateView = findViewById<TextView>(R.id.textDate)
        val rawTextView = findViewById<TextView>(R.id.textLiveTranscript)

        val playButton = findViewById<ImageView>(R.id.buttonPlay)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)

        memo?.let {
            titleView.text = it.title
            summaryView.text = it.summary
            dateView.text = it.dateTime
            rawTextView.text = it.rawText

            if (!it.audioFilePath.isNullOrEmpty()) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(it.audioFilePath)
                    prepare()
                }

                seekBar.max = mediaPlayer!!.duration

                playButton.setOnClickListener {
                    if (mediaPlayer!!.isPlaying) {
                        mediaPlayer!!.pause()
                        playButton.setImageResource(R.drawable.ic_play)
                    } else {
                        mediaPlayer!!.start()
                        playButton.setImageResource(R.drawable.ic_pause)
                        updateSeekBar()
                    }
                }

                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            mediaPlayer?.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(sb: SeekBar?) {}
                    override fun onStopTrackingTouch(sb: SeekBar?) {}
                })

            } else {
                playButton.isEnabled = false
                seekBar.isEnabled = false
            }
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

        val fabRecord = findViewById<FloatingActionButton>(R.id.fabRecord1)
        fabRecord.setOnClickListener {
            startActivity(Intent(this, RecordActivity::class.java))
        }
    }

    private fun updateSeekBar() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        findViewById<SeekBar>(R.id.seekBar).progress = it.currentPosition
                        handler.postDelayed(this, 500)
                    }
                }
            }
        }
        handler.postDelayed(runnable, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
