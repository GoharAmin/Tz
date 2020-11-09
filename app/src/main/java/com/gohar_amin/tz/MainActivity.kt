package com.gohar_amin.tz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.gohar_amin.tz.acitivities.HomeActivityWithDrawer
import com.gohar_amin.tz.acitivities.LoginActivity
import com.gohar_amin.tz.utils.PrefHelper

class MainActivity : AppCompatActivity() {
    lateinit var context: Context
    val USER_ID:String="userId"
    var prefHelper: PrefHelper?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context=this
        setContentView(R.layout.activity_main)
        prefHelper= PrefHelper.getInstance(this)
        Handler().postDelayed(object:Runnable{
            override fun run() {
                if(prefHelper!!.getString(USER_ID)!=null){
                    startActivity(Intent(context, HomeActivityWithDrawer::class.java))
                    finish()
                }else {
                    startActivity(Intent(context, LoginActivity::class.java))
                    finish()
                }
            }
        },1000)
    }
}