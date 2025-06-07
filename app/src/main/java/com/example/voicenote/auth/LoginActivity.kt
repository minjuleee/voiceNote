package com.example.voicenote.auth
import com.example.voicenote.auth.SignupActivity
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
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.voicenote.R
import com.example.voicenote.home.HomeActivity


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 로그인 버튼
        val loginButton: ImageButton = findViewById(R.id.buttonLogin)
        loginButton.setOnClickListener {
            // 로그인 로직 처리
            // 홈화면으로 가기
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        // 회원가입가기
        // 회원가입 텍스트뷰
        val signupTextView: TextView = findViewById(R.id.textSignupPrompt)
        val fullText1 = "아직 회원이 아니신가요? 회원가입하기"
        val spannable1 = SpannableString(fullText1)

        // 클릭 가능한 스팬 정의
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // 밑줄 제거
                ds.color = Color.parseColor("#0070ED") // 클릭 가능한 색상 지정
            }
        }

// "회원가입하기"에 클릭 스팬 및 색 적용
        spannable1.setSpan(clickableSpan, 14, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        signupTextView.text = spannable1
        signupTextView.movementMethod = LinkMovementMethod.getInstance()
        signupTextView.highlightColor = Color.TRANSPARENT // 클릭 시 배경색 제거


        // 푸터
        val footerTextView: TextView = findViewById(R.id.textFooter)

        val fullText = "VoiceNote+\nmade by minjuleee"
        val spannable = SpannableString(fullText)

        // "VoiceNote+" 텍스트 범위에 굵기 및 크기 적용
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(AbsoluteSizeSpan(18, true), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        footerTextView.text = spannable

    }
}
