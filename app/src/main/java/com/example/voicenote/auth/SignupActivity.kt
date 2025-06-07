package com.example.voicenote.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.voicenote.R
import com.example.voicenote.auth.LoginActivity

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // "회원이신가요? 로그인하기" 강조 + 클릭 처리
        val loginTextView: TextView = findViewById(R.id.textSignupPrompt)
        val fullText = "회원이신가요? 로그인하기"
        val spannable = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: android.view.View) {
                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.parseColor("#0070ED")
            }
        }

        spannable.setSpan(clickableSpan, 8, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        loginTextView.text = spannable
        loginTextView.movementMethod = LinkMovementMethod.getInstance()
        loginTextView.highlightColor = Color.TRANSPARENT

        // 중복확인 버튼 (추후 로직 연결 가능)
        val checkIdButton: Button = findViewById(R.id.buttonCheckId)
        val editTextId: EditText = findViewById(R.id.editTextId)
        checkIdButton.setOnClickListener {
            val userId = editTextId.text.toString()
            // 여기서 아이디 중복확인 로직 수행
        }
    }
}
