package com.gohar_amin.tz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.gohar_amin.tz.acitivities.HomeActivityWithDrawer
import com.gohar_amin.tz.acitivities.LoginActivity
import com.gohar_amin.tz.callback.ObjectCallback
import com.gohar_amin.tz.model.DeliveredOrder
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.PrefHelper
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private var id: String?=null
    lateinit var context: Context
    val USER_ID:String="userId"
    var prefHelper: PrefHelper?=null
    lateinit var deliveredRef: CollectionReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context=this
        setContentView(R.layout.activity_main)
        prefHelper= PrefHelper.getInstance(this)
        Handler().postDelayed(object:Runnable{
            override fun run() {
                id=prefHelper!!.getString(USER_ID)
                if(id!=null){
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