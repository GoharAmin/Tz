package com.gohar_amin.tz.acitivities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.gohar_amin.tz.R
import com.gohar_amin.tz.callback.ObjectCallback
import com.gohar_amin.tz.model.User
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.JsonParser
import com.gohar_amin.tz.utils.PrefHelper
import com.gohar_amin.tz.utils.Utils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() ,ObjectCallback<FirebaseUser>{
    lateinit var tvRegisterNow:TextView
    lateinit var tvForgotPassword:TextView
    lateinit var btnLogin:Button
    lateinit var userCollectionRefernce: CollectionReference
    lateinit var etPassword:EditText
    lateinit var etEmail:EditText
    lateinit var context: Context
    val USERS_REF:String="users"
    val USER_ID:String="userId"
    var prefHelper: PrefHelper?=null
    var firebaseHelper: FirebaseHelper?=null
    var user: User?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context=this;
        tvRegisterNow=findViewById(R.id.tvRegisterNow)
        tvForgotPassword=findViewById(R.id.tvForgotPassword)
        btnLogin=findViewById(R.id.btnLogin)
        etPassword=findViewById(R.id.etPassword)
        etEmail=findViewById(R.id.etEmail)
        firebaseHelper= FirebaseHelper.getInstance()
        userCollectionRefernce= FirebaseFirestore.getInstance().collection(USERS_REF)
        prefHelper= PrefHelper.getInstance(this)
        btnLogin.setOnClickListener {
            if(checkInput()) {
                Utils.showDialog(this)
                user = User()
                user!!.email=etEmail.text.toString()
                user!!.password=etPassword.text.toString()
                firebaseHelper!!.emailAndPasswordLogin(user!!.email!!,user!!.password!!,this@LoginActivity)
            }
        }
        tvRegisterNow.setOnClickListener {
            startActivity(Intent(context,RegistrationActivity::class.java))
        }
    }

    private fun checkInput(): Boolean {
        if(TextUtils.isEmpty(etEmail.text)){
            Toast.makeText(context, "Please enter the Email", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(TextUtils.isEmpty(etPassword.text)){
            Toast.makeText(context, "Please enter the Password", Toast.LENGTH_SHORT).show()
            return false
        }
        else{
            return true
        }

    }

    override fun onError(msg: String?) {
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
    }

    override fun onData(t: FirebaseUser?) {
        t?.let {
            if(user!=null) {
                firebaseHelper!!.getSingleFireStore(userCollectionRefernce,it.uid,User::class.java,object :ObjectCallback<User>{
                    override fun onError(msg: String?) {
                        Utils.dismissDialog()
                    }

                    override fun onData(t: User?) {
                        Utils.dismissDialog()
                        if(t!=null){
                            Utils.dismissDialog()
                            prefHelper!!.saveString(USER_ID,""+t.id)
                            prefHelper!!.saveString("me", JsonParser.toJson(t))
                            startActivity(Intent(context, HomeActivityWithDrawer::class.java))
                        }
                    }
                })

            }
        }
    }

}