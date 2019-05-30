package com.example.firebase_master

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
        buttonListener()
    }

    fun init(){

    }

    fun buttonListener() {
        emailLogin.setOnClickListener {
            if (email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
                // 텍스트 입력했을경우

            } else {
                toast("이메일과 비밀번호를 알맞게 입력해주세요.")
            }

        }

        facebookLogin.setOnClickListener {

        }

        googleLogin.setOnClickListener {

        }
        twitterLogin.setOnClickListener {

        }
    }
}
