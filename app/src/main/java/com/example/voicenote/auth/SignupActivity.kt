package com.example.voicenote.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.TextPaint
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voicenote.R
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val signupButton: ImageButton = findViewById(R.id.buttonSignup)


        // ✅ 회원가입 버튼 클릭 처리
        signupButton.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isNotEmpty() && password.length >= 6) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "회원가입 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "이메일과 6자리 이상 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ "로그인하기" 텍스트 클릭 처리
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
    }
}
