package com.gohar_amin.tz.acitivities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.adapters.CheckoutAdapter
import com.gohar_amin.tz.callback.ObjectCallback
import com.gohar_amin.tz.model.Product
import com.gohar_amin.tz.model.User
import com.gohar_amin.tz.model.UserGig
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.PrefHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class CheckoutActivity : AppCompatActivity() {
    val GIGS:String="gigs"
    lateinit var gigCollectionRef:CollectionReference
    private var userGig: UserGig?=null
    private var user: User?=null
    private lateinit var tvPrice:TextView
    private lateinit var tvProductName:TextView
    lateinit var recyclerView:RecyclerView
    lateinit var adapter: CheckoutAdapter
    lateinit var list:ArrayList<Product>
    lateinit var btnBuyNow:Button
    lateinit var firebaseHelper: FirebaseHelper
    lateinit var collectionRef:CollectionReference
    lateinit var notificationRef:CollectionReference
    lateinit var context:Context
    lateinit var dialog:ProgressDialog
    lateinit var tvTotal:TextView
    var fcm:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context=this
        setContentView(R.layout.activity_checkout)
        //recyclerView=findViewById(R.id.recyclerView);
        tvPrice=findViewById(R.id.tvPrice);
        btnBuyNow=findViewById(R.id.btnBuyNow);
        tvProductName=findViewById(R.id.tvProductName);
        tvTotal=findViewById(R.id.tvTotal);
        btnBuyNow=findViewById(R.id.btnBuyNow)
        //recyclerView.setHasFixedSize(true)
        Log.e("users",""+intent.getStringExtra("user"))
        user=Gson().fromJson(intent.getStringExtra("user"),User::class.java)
        userGig=Gson().fromJson(intent.getStringExtra("userGig"),UserGig::class.java)
        firebaseHelper= FirebaseHelper.getInstance()!!
        collectionRef=FirebaseFirestore.getInstance().collection("orders")
        notificationRef=FirebaseFirestore.getInstance().collection("ordersNotication")
        gigCollectionRef=FirebaseFirestore.getInstance().collection(GIGS)
        fcm=PrefHelper.getInstance(this)!!.getString("fcm")
        userGig!!.purchased=userGig!!.purchased+1
        tvPrice.setText(""+userGig!!.cost);
        tvProductName.setText(""+userGig!!.title);
        tvTotal.setText("$ "+userGig!!.cost);
        dialog= ProgressDialog(context)
        dialog.setCancelable(false);
        dialog.setTitle("Please wait ...")
        dialog.setMessage("Your order is gonna placed...")
        btnBuyNow.setOnClickListener {
            firebaseHelper.saveFireStoreDb(
                    gigCollectionRef,
                    userGig!!.id!!,
                    userGig!!,
                    object : ObjectCallback<String> {
                        override fun onError(msg: String?) {
                            dialog.dismiss()
                            Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onData(t: String?) {
                            buyNow()
                        }
                    })

        }
    }

    private fun buyNow() {
        val map = HashMap<String, Any>()
        map.put("user", FirebaseAuth.getInstance().currentUser!!.uid);
        map.put("seller",user!!.id!!)
        map.put("purchasedGig", userGig!!);
        firebaseHelper.saveFireStoreDb(
            collectionRef,
            System.currentTimeMillis().toString(),
            map,
            object : ObjectCallback<String> {
                override fun onError(msg: String?) {
                    dialog.dismiss()
                    Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
                }

                override fun onData(t: String?) {
                    saveNotifaction(map)

                }
            })
    }

    private fun saveNotifaction(map: java.util.HashMap<String, Any>) {
        map.put("fcm",user!!.fcm!!)
        firebaseHelper.saveFireStoreDb(notificationRef, FirebaseAuth.getInstance().currentUser!!.uid, map,object : ObjectCallback<String> {
            override fun onError(msg: String?) {
                Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
            }

            override fun onData(t: String?) {
                dialog.dismiss()
                Toast.makeText(context, "Your Order has benn added successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@CheckoutActivity, HomeActivityWithDrawer::class.java));
            }
        })
    }

    /*private fun setUpList() {
        list= ArrayList();
        val p1:Product= Product()
        p1.price=200
        p1.name="abc"
        p1.qty=1
        val p2:Product=Product()
        p2.price=200
        p2.name="abc"
        p2.qty=1

        val p3:Product=Product()
        p3.price=200
        p3.name="abc"
        p3.qty=1

list.add(p1)
list.add(p2)
list.add(p3)
    }*/
}