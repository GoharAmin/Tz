package com.gohar_amin.tz.acitivities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.acitivities.ui.ChatActivity
import com.gohar_amin.tz.adapters.ProductReviewsAdapter
import com.gohar_amin.tz.callback.ObjectCallback
import com.gohar_amin.tz.model.Product
import com.gohar_amin.tz.model.User
import com.gohar_amin.tz.model.UserGig
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.JsonParser
import com.gohar_amin.tz.utils.PrefHelper
import com.gohar_amin.tz.utils.Utils
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class GigDetailActivity : AppCompatActivity(), ObjectCallback<User> {
    private var isCheck: Boolean = false
    lateinit var recyclerview: RecyclerView
    lateinit var adapter: ProductReviewsAdapter
    lateinit var list: ArrayList<Product>
    lateinit var btnOrderNow: Button
    lateinit var context: Context
    lateinit var userGig: UserGig
    val USERS_REF: String = "users"
    lateinit var collectionRef: CollectionReference
    lateinit var firebaseHelper: FirebaseHelper
    lateinit var imageView: ImageView
    lateinit var ivProfile: ImageView
    lateinit var imageViewChat: ImageView
    lateinit var tvName: TextView
    lateinit var tvDesignation: TextView
    lateinit var tvDecription: TextView
    lateinit var tvPricePerDay: TextView
    lateinit var tvCountry: TextView
    lateinit var checkBox: CheckBox
    lateinit var prefHelper: PrefHelper
    val USER_ID:String="userId"
    var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_gig_detail)
        recyclerview = findViewById(R.id.rvRecyclerview)
        tvDesignation = findViewById(R.id.tvDesignation)
        tvName = findViewById(R.id.tvName)
        tvDecription = findViewById(R.id.tvDecription)
        imageViewChat = findViewById(R.id.imageViewChat)
        tvPricePerDay = findViewById(R.id.tvPricePerDay)
        btnOrderNow = findViewById(R.id.btnOrderNow)
        imageView = findViewById(R.id.imageView)
        ivProfile = findViewById(R.id.ivProfile)
        tvCountry = findViewById(R.id.tvCountry)
        checkBox = findViewById(R.id.checkBox)
        recyclerview.setHasFixedSize(true)
        firebaseHelper = FirebaseHelper.getInstance()!!
        collectionRef = FirebaseFirestore.getInstance().collection(USERS_REF)
        prefHelper= PrefHelper.getInstance(this)!!
        var gigExtra = intent.getStringExtra("gigDetail")
        userGig = Gson().fromJson(gigExtra, UserGig::class.java)
        Utils.loadImage(context, userGig.ImageUrl, imageView)
        tvDesignation.setText(userGig.workOptions)
        tvDecription.setText(userGig.detail)
        if(userGig.fastDeliverProduct!=null) {
            checkBox.setText("i can " + userGig.fastDeliverProduct)
        }else{
            checkBox.setText("i can build" )
        }
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            isCheck = isChecked
        }
        tvPricePerDay.setText("$" + userGig.cost + " in " + userGig.fastDeliverPrice + " Dats")
        firebaseHelper.getSingleFireStoreByUid(
            collectionRef,
            userGig.uid!!,
            User::class.java,
            this@GigDetailActivity
        )
        setUpList();
 /*       recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = ProductReviewsAdapter(this, list)
        recyclerview.adapter = adapter*/
        btnOrderNow.setOnClickListener {
            //if (isCheck) {
                Log.e("order",Gson().toJson(user))
                val id=prefHelper.getString(USER_ID)
                if(!id!!.equals(user!!.id)) {
                    val i = Intent(context, CheckoutActivity::class.java)
                    i.putExtra("user", Gson().toJson(user))
                    i.putExtra("userGig", Gson().toJson(userGig))
                    startActivity(i)
                }else{
                    Toast.makeText(context, "You are not capable to order your own Gig", Toast.LENGTH_SHORT).show()
                }
            /*} else {
                Toast.makeText(context, "Please select the option", Toast.LENGTH_SHORT).show()
            }*/
        }
        imageViewChat.setOnClickListener {
            val id=prefHelper.getString(USER_ID)
            if(!id!!.equals(user!!.id)) {
                if (userGig != null && user != null) {
                    startActivity(
                            Intent(
                                    context,
                                    ChatActivity::class.java
                            ).putExtra("openChat", userGig.uid)
                                    .putExtra("recevierUser", JsonParser.toJson(user!!))
                    )
                }
            }else{
                Toast.makeText(context, "You are not allowed to chat yourself", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpList() {
        list = ArrayList();
        val r1: Product = Product()
        r1.name = "abc"
        r1.type = "excellent"
        r1.time = "2 days ago"
        val r2: Product = Product()
        r2.name = "xyz"
        r2.type = "very Good"
        r2.time = "25 days ago"
        val r3: Product = Product()
        r3.name = "abc"
        r3.type = "Good"
        r3.time = "3 days ago"
        list.add(r1)
        list.add(r2)
        list.add(r3)
    }

    override fun onError(msg: String?) {
        Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
    }

    override fun onData(t: User?) {
        t?.let { //           Utils.loadImage(context,userGig.ImageUrl,imageView)
            user = it
            Log.e("onData",Gson().toJson(user))
            Utils.loadImage(context,user!!.imageUrl,ivProfile)
            tvCountry.setText("" + user!!.country)
            tvName.setText(""+user!!.name)
        }
    }
}