package com.gohar_amin.tz.acitivities

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class RegistrationActivity : AppCompatActivity(),ObjectCallback<FirebaseUser> {
    private var imageUrl: Uri?=null
    private var imageName: String?=null
    lateinit var etName:EditText
    lateinit var etEmail:EditText
    lateinit var etCountry:EditText
    lateinit var etLangauge:EditText
    lateinit var etPassword:EditText
    lateinit var btnRegister:Button
    lateinit var tvMember:TextView
    lateinit var context:Context
    lateinit var ivEdit:ImageView
    lateinit var ivProfile:ImageView
    lateinit var storageReference: StorageReference
     val USER_ID:String="userId"
    val USERS_REF:String="users"
    var firebaseHelper: FirebaseHelper?=null
    var prefHelper: PrefHelper?=null
    lateinit var userCollectionRefernce:CollectionReference
    lateinit var user:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        context=this
        etName=findViewById(R.id.etName)
        etEmail=findViewById(R.id.etEmail)
        etCountry=findViewById(R.id.etCountry)
        etLangauge=findViewById(R.id.etLangauge)
        etPassword=findViewById(R.id.etPassword)
        btnRegister=findViewById(R.id.btnRegister)
        tvMember=findViewById(R.id.tvMember)
        ivProfile=findViewById(R.id.ivProfile)
        ivEdit=findViewById(R.id.ivEdit)
        storageReference=FirebaseStorage.getInstance().getReference("userProfiles")
        firebaseHelper= FirebaseHelper.getInstance()
        prefHelper= PrefHelper.getInstance(this)
        userCollectionRefernce=FirebaseFirestore.getInstance().collection(USERS_REF)
        ivEdit.setOnClickListener {
            Utils.showSelectedImages(this@RegistrationActivity)
        }
        user= User()
        btnRegister.setOnClickListener {
            if(checkInput()) {
                Utils.showDialog(this)
                user.email = etEmail.text.toString()
                user.password = etPassword.text.toString()
                user.name=etName.text.toString()
                user.country=etCountry.text.toString()
                user.language=etLangauge.text.toString()
                firebaseHelper!!.emailAndPasswordCreate(
                    user.email!!,
                    user.password!!,
                    this@RegistrationActivity
                )

            }
        }
        tvMember.setOnClickListener {
            startActivity(Intent(context,LoginActivity::class.java))
        }
    }
    private fun checkInput(): Boolean {
        if(TextUtils.isEmpty(etEmail.text)){
            Toast.makeText(context, "Please enter the Email", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(imageUrl==null || imageName==null){
            Toast.makeText(context, "Please Select an Image", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(TextUtils.isEmpty(etPassword.text)){
            Toast.makeText(context, "Please enter the PAssword", Toast.LENGTH_SHORT).show()
            return false
        }else if(TextUtils.isEmpty(etName.text)){
            Toast.makeText(context, "Please enter the Name", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(TextUtils.isEmpty(etCountry.text)){
            Toast.makeText(context, "Please enter the Country", Toast.LENGTH_SHORT).show()
            return false
        }else if(TextUtils.isEmpty(etLangauge.text)){
            Toast.makeText(context, "Please enter the Language", Toast.LENGTH_SHORT).show()
            return false
        }
        else{
            return true
        }

    }
    override fun onError(msg: String?) {
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
    }

    override fun onData(firebaseUser: FirebaseUser?) {
        firebaseUser?.let {
            user.id=it.uid
            uploadUserImage()

        }
    }

    private fun uploadUserImage() {
        Utils.uploadImage(
            context,
            imageUrl!!,
            imageName,
            storageReference,
            object : ObjectCallback<Uri?> {
                override fun onError(msg: String?) {
                    Utils.dismissDialog()
                    Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
                }

                override fun onData(t: Uri?) {
                    user.imageUrl=t.toString();
                    saveUser()
                }
            })
    }

    private fun saveUser() {
        firebaseHelper!!.saveFireStoreDb(
            userCollectionRefernce,
            user.id!!,
            user,
            object : ObjectCallback<String> {
                override fun onError(msg: String?) {
                    Utils.dismissDialog()
                    Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
                }

                override fun onData(t: String?) {
                    Utils.dismissDialog()
                    prefHelper!!.saveString(USER_ID,""+user.id)
                    prefHelper!!.saveString("me", JsonParser.toJson(user))
                    startActivity(Intent(context, HomeActivityWithDrawer::class.java))
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==10001 &&resultCode == RESULT_OK && data != null) {
            val list: List<Image>? = ImagePicker.getImages(data)
            if (list != null && !list.isEmpty()) {
                imageUrl= Uri.fromFile(File(list[0].getPath()));
                imageName=list[0].getName()
                Utils.loadImage(context,imageUrl.toString(),ivProfile)
            }
        }
        if (requestCode==201 &&resultCode == RESULT_OK && data != null) {
            imageUrl=data.data!!
            Log.e("uri",""+imageUrl)
            imageName=data.data!!.lastPathSegment
            //ivProfile.setImageURI(imageUrl)
            Utils.loadImage(context,imageUrl.toString(),ivProfile)
        }
    }
}