package com.example.voicenote.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voicenote.R
import com.example.voicenote.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth // ✅ Firebase 인증 객체 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance() // ✅ 초기화

        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val loginButton: ImageButton = findViewById(R.id.buttonLogin)

        // ✅ 로그인 버튼 클릭 시 Firebase 로그인 처리
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ 회원가입 텍스트 클릭 시 이동
        val signupTextView: TextView = findViewById(R.id.textSignupPrompt)
        val fullText1 = "아직 회원이 아니신가요? 회원가입하기"
        val spannable1 = SpannableString(fullText1)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: android.view.View) {
                startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.parseColor("#0070ED")
            }
        }

        spannable1.setSpan(clickableSpan, 14, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        signupTextView.text = spannable1
        signupTextView.movementMethod = LinkMovementMethod.getInstance()
        signupTextView.highlightColor = Color.TRANSPARENT

        // ✅ 푸터 텍스트 꾸미기
        val footerTextView: TextView = findViewById(R.id.textFooter)
        val fullText = "VoiceNote+\nmade by minjuleee"
        val spannable = SpannableString(fullText)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(AbsoluteSizeSpan(18, true), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        footerTextView.text = spannable
    }
}
