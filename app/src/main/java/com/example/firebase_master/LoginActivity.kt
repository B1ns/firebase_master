package com.example.firebase_master

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val GOOGLE_LOGIN_CODE = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()

        buttonListener()

        printHashKey()
    }

    fun init() {
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun createAndLoginEmail() { //Firebase Authentication
        if (email.text?.trim().toString().isNotEmpty() && password.text?.trim().toString().isNotEmpty()) {
            auth.createUserWithEmailAndPassword(
                email.text?.trim().toString(),
                password.text?.trim().toString()
            )
                .addOnCompleteListener { task: Task<AuthResult> ->
                    when {
                        //계정이 없을경우(회원가입)
                        task.isSuccessful -> { //바로 로그인까지 처리
                            moveMainPage(auth.currentUser, "회원가입 및 로그인 성공.")
                        }
                        //계정이 있고 에러가 난 경우
                        task.exception?.message.isNullOrEmpty() -> Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                        //계정이 있고 에러가 나지 않은 경우(로그인 메소드 호출)
                        else -> signinEmail()
                    }
                }
        } else {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show()
        }
    }

    fun buttonListener() {
        emailLogin.setOnClickListener {
            if (email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
                // 텍스트 입력했을경우
                createAndLoginEmail()

            } else {
                toast("이메일과 비밀번호를 알맞게 입력해주세요.")
            }

        }

        facebookLogin.setOnClickListener {

        }

        googleLogin.setOnClickListener {
            googleLogin()
        }
        twitterLogin.setOnClickListener {

        }
    }


    private fun googleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        //idToken으로 인증서를 받고 인증서를 통한 로그인
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                moveMainPage(auth.currentUser)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_LOGIN_CODE && resultCode == Activity.RESULT_OK) {
            //구글 로그인 결과값을 받아옴
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                //계정 정보를 넘김
                firebaseAuthWithGoogle(result.signInAccount!!)
            }
        }
    }

    private fun signinEmail() { //Firebase Authentication
        auth.signInWithEmailAndPassword(email.text?.trim().toString(), password.text?.trim().toString())
            .addOnCompleteListener { task: Task<AuthResult> ->
                when {
                    task.isSuccessful -> {
                        moveMainPage(auth.currentUser)
                    }
                    else -> Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun moveMainPage(user: FirebaseUser?, message: String = "") {
        if (user != null) {
            if (message.isEmpty()) {
                Toast.makeText(this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

//    private fun printHashKey() { //facebook key hash
//        try {
//            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val hashKey = String(Base64.encode(md.digest(), 0))
//                Log.d("asdf", "printHashKey() Hash Key: $hashKey")
//            }
//        } catch (e: NoSuchAlgorithmException) {
//            Log.d("asdf", "printHashKey()", e)
//        } catch (e: Exception) {
//            Log.d("asdf", "printHashKey()", e)
//        }
//    }
}