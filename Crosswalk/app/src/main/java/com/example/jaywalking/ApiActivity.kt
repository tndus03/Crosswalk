package com.example.jaywalking

import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.jaywalking.databinding.ActivityApiBinding
import com.example.jaywalking.databinding.ActivityMainBinding

class ApiActivity : AppCompatActivity() {
    lateinit var binding : ActivityApiBinding
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_api)

        binding = ActivityApiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val xmlfragment = XmlFragment()
        //val bundle = Bundle()

        binding.searchBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.activityContent, xmlfragment)
                .commit()
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val bgColor = sharedPreferences.getString("color", "")
        binding.apiLayout.setBackgroundColor(Color.parseColor(bgColor))
    }

    override fun onResume() {
        super.onResume()
        val bgColor = sharedPreferences.getString("color", "")
        binding.apiLayout.setBackgroundColor(Color.parseColor(bgColor))
    }
}