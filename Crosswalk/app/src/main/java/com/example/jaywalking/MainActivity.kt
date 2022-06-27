package com.example.jaywalking

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.example.jaywalking.databinding.ActivityMainBinding
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            if (binding.btnLogin.text.equals("로그인"))
                intent.putExtra("data", "logout")
            else if (binding.btnLogin.text.equals("로그아웃"))
                intent.putExtra("data", "login")
                startActivity(intent)
        }

        binding.lookBtn.setOnClickListener {
            val intent = Intent(this, ApiActivity::class.java)
            startActivity(intent)
        }

        myCheckPermission(this)
        binding.reportBtn.setOnClickListener {
            if (MyApplication.checkAuth() || MyApplication.email != null) {
                startActivity(Intent(this, AddActivity::class.java))
            }
            else {
                Toast.makeText(this, "인증 필요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.listBtn.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        binding.setup.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val bgColor = sharedPreferences.getString("color", "")
        binding.mainLayout.setBackgroundColor(Color.parseColor(bgColor))

        val nickName = sharedPreferences.getString("id", "")
        if (!nickName.isNullOrBlank()) {
            if (MyApplication.checkAuth() || MyApplication.email != null)
                binding.authTv.text = nickName
            else
                binding.authTv.text = ""
        }
    }

    override fun onResume() {
        super.onResume()
        val bgColor = sharedPreferences.getString("color", "")
        binding.mainLayout.setBackgroundColor(Color.parseColor(bgColor))
        val nickName = sharedPreferences.getString("id", "")
        if (!nickName.isNullOrBlank()) {
            if (MyApplication.checkAuth() || MyApplication.email != null)
                binding.authTv.text = nickName
            else
                binding.authTv.text = ""
        }
    }

    override fun onStart() {
        super.onStart()
        if (MyApplication.checkAuth() || MyApplication.email != null) {
            binding.btnLogin.text = "로그아웃"
            binding.authTv.text = "${MyApplication.email}님"
            binding.authTv.textSize = 16F
        }
        else {
            binding.btnLogin.text = "로그인"
            binding.authTv.text = ""
        }
    }
}