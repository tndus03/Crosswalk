package com.example.jaywalking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.jaywalking.databinding.ActivityAddBinding
import com.example.jaywalking.databinding.ActivityApiBinding
import java.io.File
import java.util.*

class AddActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddBinding
    lateinit var filePath : String
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AddGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
        }

        binding.AddSave.setOnClickListener {
            if (binding.addImageView.drawable !== null && binding.addEditView.text.isNotEmpty() && binding.addr.text.isNotEmpty()) {
                // save 동작 진행
                saveStore()

                // 알림
                val notification = sharedPreferences.getString("noti", "")
                if (notification.equals("YES")) {
                    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    val builder: NotificationCompat.Builder

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val ch_id = "Add"
                        val channel = NotificationChannel(
                            ch_id,
                            "Add Write",
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                        channel.description = "글 작성 알림"
                        channel.setShowBadge(true)
                        channel.enableLights(true)
                        channel.lightColor = Color.RED

                        manager.createNotificationChannel(channel)
                        builder = NotificationCompat.Builder(this, ch_id)
                    }
                    else {
                        builder = NotificationCompat.Builder(this)
                    }

                    builder.setSmallIcon(R.drawable.check)
                    builder.setWhen(System.currentTimeMillis())
                    builder.setContentTitle(MyApplication.email + "님")
                    builder.setContentText("글 작성이 완료되었습니다.")

                    manager.notify(11, builder.build())
                }
            }
            else {
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val requestLauncherr: ActivityResultLauncher<Intent>
         = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
             it.data!!.getStringExtra("result")?.let {
                 binding.addr.setText(it!!)
             }
        }
        binding.addSearch.setOnClickListener {
            val intent = Intent(this@AddActivity, WebViewActivity::class.java)
            requestLauncherr.launch(intent)
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val bgColor = sharedPreferences.getString("color", "")
        binding.addLayout.setBackgroundColor(Color.parseColor(bgColor))
    }

    override fun onResume() {
        super.onResume()
        val bgColor = sharedPreferences.getString("color", "")
        binding.addLayout.setBackgroundColor(Color.parseColor(bgColor))
    }

    val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode === android.app.Activity.RESULT_OK) {
            Glide
                .with(applicationContext)
                .load(it.data?.data)
                .apply(RequestOptions().override(250, 200))
                .centerCrop()
                .into(binding.addImageView)
            val cursor = contentResolver.query(it.data?.data as Uri,
                arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null)
            cursor?.moveToFirst().let {
                filePath = cursor?.getString(0) as String
            }
        }
    }

    private fun saveStore() {
        val data = mapOf(
            "email" to MyApplication.email,
            "content" to binding.addEditView.text.toString(),
            "address" to binding.addr.text.toString(),
            "detailAddress" to binding.detailAddr.text.toString(),
            "date" to dateToString(Date())
        )

        MyApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {
                uploadImage(it.id)
            }
            .addOnFailureListener {
                Log.d("mobileApp", "data save error")
            }
    }

    private fun uploadImage(docId:String) {
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${docId}.jpg")

        val file = Uri.fromFile(File(filePath))
        imageRef.putFile(file)
            .addOnSuccessListener {
                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ReportActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("mobileApp", "file save error")
            }
    }
}