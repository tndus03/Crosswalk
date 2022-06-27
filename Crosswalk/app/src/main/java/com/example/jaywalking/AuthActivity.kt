package com.example.jaywalking

import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.example.jaywalking.databinding.ActivityAuthBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_auth)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeVisibility(intent.getStringExtra("data").toString())
        binding.goSignInBtn.setOnClickListener {
            changeVisibility("signin")
        }

        binding.signBtn.setOnClickListener {
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if (task.isSuccessful) {
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { sendTask ->
                                if (sendTask.isSuccessful) {
                                    Toast.makeText(
                                        baseContext,
                                        "회원가입 성공! 메일 확인 요망",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    changeVisibility("logout")
                                } else {
                                    Toast.makeText(baseContext, "메일 발송 실패", Toast.LENGTH_SHORT)
                                        .show()
                                    changeVisibility("logout")
                                }
                            }
                    } else {
                        Toast.makeText(baseContext, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        changeVisibility("logout")
                    }
                }
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            MyApplication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if (task.isSuccessful) {
                        if (MyApplication.checkAuth()) {
                            MyApplication.email = email
                            finish()
                        } else {
                            Toast.makeText(baseContext, "이메일 인증 실패", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(baseContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.logoutBtn.setOnClickListener {
            MyApplication.auth.signOut()
            MyApplication.email = null

            finish()
        }

        binding.kakaoLogin.setOnClickListener {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.e("mobileApp", "토큰 정보 보기 실패", error)
                } else if (tokenInfo != null) {
                    Log.i("mobileApp", "토큰 정보 보기 성공")
                    finish()
                }
            }

            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("mobileApp", "카카오계정으로 로그인 실패", error)
                }
                else if (token != null) {
                    Log.i("mobileApp", "카카오계정으로 로그인 성공 ${token.accessToken}")
                    // 사용자 정보 요청 (기본)
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e("mobileApp", "사용자 정보 요청 실패", error)
                        }
                        else if (user != null) {
                            Log.i("mobileApp", "사용자 정보 요청 성공 ${user.kakaoAccount?.email}")
                            var scopes = mutableListOf<String>()
                            if(user.kakaoAccount?.email != null) {  // 이메일을 가져왔다면
                                MyApplication.email = user.kakaoAccount?.email
                                finish()
                            }
                            else if(user.kakaoAccount?.emailNeedsAgreement == true){
                                Log.i("mobileApp", "사용자에게 추가 동의 필요")
                                scopes.add("account_email")
                                UserApiClient.instance.loginWithNewScopes(this, scopes) { token, error ->
                                    if(error != null) {
                                        Log.e("mobileApp", "추가 동의 실패", error)
                                    }
                                    else {
                                        UserApiClient.instance.me { user, error ->
                                            if(error != null){
                                                Log.e("mobileApp", "사용자 정보 요청 실패", error)
                                            }
                                            else if(user != null){
                                                MyApplication.email = user.kakaoAccount?.email.toString()
                                                finish()
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                Log.e("mobileApp", "이메일 획득 불가", error)
                            }
                        }
                    }
                }
            }
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            }
            else {  // 사용자의 폰에 카카오톡이 없는 경우
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val bgColor = sharedPreferences.getString("color", "")
        binding.authLayout.setBackgroundColor(Color.parseColor(bgColor))
    }

    fun changeVisibility(mode: String) {
        if (mode.equals("login")) {  // 로그인 상태
            binding.run {
                authMainTextView.text = "정말 로그아웃하시겠습니까?"
                authMainTextView.textSize = 24F
                authMainTextView.visibility = View.VISIBLE
                logoutBtn.visibility = View.VISIBLE
                goSignInBtn.visibility = View.GONE
                authEmailEditView.visibility = View.GONE
                authPasswordEditView.visibility = View.GONE
                signBtn.visibility = View.GONE
                loginBtn.visibility = View.GONE
                kakaoLogin.visibility = View.GONE
            }
        } else if (mode.equals("logout")) {  // 로그아웃 상태
            binding.run {
                authMainTextView.text = "SIGN IN"
                authMainTextView.visibility = View.VISIBLE
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.VISIBLE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.GONE
                loginBtn.visibility = View.VISIBLE
                kakaoLogin.visibility = View.VISIBLE
            }
        } else if (mode.equals("signin")) {  // 회원가입 중
            binding.run {
                authMainTextView.text = "SIGN UP"
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.VISIBLE
                loginBtn.visibility = View.GONE
                kakaoLogin.visibility = View.GONE
            }
        }
    }
}