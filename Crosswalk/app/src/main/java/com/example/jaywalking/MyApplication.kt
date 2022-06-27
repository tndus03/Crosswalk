package com.example.jaywalking

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.kakao.sdk.common.KakaoSdk
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Retrofit

class MyApplication:MultiDexApplication() {
    companion object {
        lateinit var auth: FirebaseAuth
        var email:String? = null

        lateinit var db : FirebaseFirestore
        lateinit var storage : FirebaseStorage

        fun checkAuth() : Boolean {
            var currentUser = auth.currentUser
            return currentUser?.let {
                email = currentUser.email
                currentUser.isEmailVerified
            }?: let {
                false
            }
        }

        var networkServiceXml : NetworkService
        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()
        val retrofitXml : Retrofit
            get() = Retrofit.Builder()
                .baseUrl("https://openapi.gg.go.kr/")
                .addConverterFactory(TikXmlConverterFactory.create(parser))
                .build()

        init {
            networkServiceXml = retrofitXml.create(NetworkService::class.java)
        }

    }

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
        KakaoSdk.init(this, "4bad8449d6327f90b90fd2d1d079e115")

        db = FirebaseFirestore.getInstance()
        storage = Firebase.storage
    }
}