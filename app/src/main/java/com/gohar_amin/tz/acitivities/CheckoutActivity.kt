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
import com.gohar_amin.tz.model.*
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.JsonParser
import com.gohar_amin.tz.utils.PrefHelper
import com.gohar_amin.tz.utils.Utils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class CheckoutActivity : AppCompatActivity() {
    val GIGS: String = "gigs"
    lateinit var gigCollectionRef: CollectionReference
    lateinit var shippedReference: CollectionReference
    lateinit var chatUsers: CollectionReference
    private var userGig: UserGig? = null
    private var user: User? = null
    private lateinit var tvPrice: TextView
    private lateinit var tvProductName: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: CheckoutAdapter
    lateinit var list: ArrayList<Product>
    lateinit var btnBuyNow: Button
    lateinit var firebaseHelper: FirebaseHelper
    lateinit var collectionRef: CollectionReference
    lateinit var notificationRef: CollectionReference
    lateinit var context: Context
    lateinit var dialog: ProgressDialog
    lateinit var tvTotal: TextView
    lateinit var chatReference: CollectionReference
    lateinit var receiveReference: CollectionReference
    var senderId: String? = null

    var fcm: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_checkout)
        //recyclerView=findViewById(R.id.recyclerView);
        tvPrice = findViewById(R.id.tvPrice);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        tvProductName = findViewById(R.id.tvProductName);
        tvTotal = findViewById(R.id.tvTotal);
        btnBuyNow = findViewById(R.id.btnBuyNow)
        //recyclerView.setHasFixedSize(true)
        Log.e("users", "" + intent.getStringExtra("user"))
        user = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
        userGig = Gson().fromJson(intent.getStringExtra("userGig"), UserGig::class.java)
        firebaseHelper = FirebaseHelper.getInstance()!!
        collectionRef = FirebaseFirestore.getInstance().collection("orders")
        notificationRef = FirebaseFirestore.getInstance().collection("ordersNotication")
        gigCollectionRef = FirebaseFirestore.getInstance().collection(GIGS)
        chatUsers = FirebaseFirestore.getInstance().collection("chatUsers")
        shippedReference=FirebaseFirestore.getInstance().collection("deliveredOrder");

        fcm = PrefHelper.getInstance(this)!!.getString("fcm")
        senderId = FirebaseAuth.getInstance().currentUser!!.uid
        chatReference =
                FirebaseFirestore.getInstance().collection("chat").document(senderId!!)
                        .collection(user!!.id!!)
        receiveReference =
                FirebaseFirestore.getInstance().collection("chat").document(user!!.id!!)
                        .collection(senderId!!)
        userGig!!.purchased = userGig!!.purchased + 1
        tvPrice.setText("" + userGig!!.cost);
        tvProductName.setText("" + userGig!!.title);
        tvTotal.setText("$ " + userGig!!.cost);
        dialog = ProgressDialog(context)
        dialog.setCancelable(false);
        dialog.setTitle("Please wait ...")
        dialog.setMessage("Your order is gonna placed...")
        btnBuyNow.setOnClickListener {
            dialog.show()
            firebaseHelper.saveFireStoreDb(
                    gigCollectionRef,
                    userGig!!.id!!,
                    userGig!!,
                    object : ObjectCallback<String> {
                        override fun onError(msg: String?) {
                            dialog.dismiss()
                            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onData(t: String?) {
                            val chat = Chat(senderId, user!!.id!!, "" + System.currentTimeMillis(), Utils.TEXT_MESSAGE)
                            chat.messageTypeId = Utils.TEXT_MESSAGE
                            chat.userName = FirebaseAuth.getInstance().currentUser!!.email
                            chat.text = "New Order from the " + user!!.name!! + " of  " + userGig!!.title
                            chat.clickable = user!!.id!!
                            send(chat)
                        }
                    })

        }
    }

    private fun buyNow(id: String) {
        val map = HashMap<String, Any>()
        map.put("user", FirebaseAuth.getInstance().currentUser!!.uid);
        map.put("seller", user!!.id!!)
        map.put("purchasedGig", userGig!!);
        firebaseHelper.saveFireStoreDb(
                collectionRef,
                id,
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
        map.put("fcm", user!!.fcm!!)
        firebaseHelper.saveFireStoreDb(notificationRef, FirebaseAuth.getInstance().currentUser!!.uid, map, object : ObjectCallback<String> {
            override fun onError(msg: String?) {
                Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
            }

            override fun onData(t: String?) {
               order()
            }
        })
    }

    private fun send(chat: Chat) {
        val time = "" + System.currentTimeMillis()
        val model = GeneralModel()
        //model.users = JsonParser.toJson(allUserlist)
        val userPref: String? = PrefHelper.getInstance(this@CheckoutActivity)!!.getString("me")
        val sender: User? = JsonParser.toObject(userPref, User::class.java)
        chatUsers.document(senderId!!).collection("msg").document(user!!.id!!).set(user!!)
                .addOnCompleteListener { t ->
                    if (t.isSuccessful) {
                        chatUsers.document(user!!.id!!).collection("msg").document(senderId!!).set(sender!!)
                                .addOnCompleteListener { tk ->
                                    if (tk.isSuccessful) {
                                        canSent(time, "" + System.currentTimeMillis(), chat)

                                    } else {
                                        dialog.dismiss()
                                        Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
                                    }
                                }
                    } else {
                        Utils.dismissDialog()
                        Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
                    }
                }

    }

    private fun canSent(id: String, time: String, chat: Chat) {
        chatReference.document(time).set(chat).addOnCompleteListener { task: Task<Void> ->
            if (task.isSuccessful) {
                receiveReference.document(time).set(chat).addOnCompleteListener { task1: Task<Void> ->
                    if (task.isSuccessful) {
                        buyNow(id)
                    } else {
                        dialog.dismiss()
                        Toast.makeText(context, "" + task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "" + task.exception!!.message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()

            }
        }

    }
    fun order(){
        val order = DeliveredOrder()
        order.seller = user!!.id!!
        order.buyer =FirebaseAuth.getInstance().currentUser!!.uid
        order.isDelivered = false
        order.isReview = false
        order.name=user!!.name
        order.id=user!!.id!!
        FirebaseHelper.getInstance()!!.saveFireStoreDb(shippedReference, order.buyer!!, order, object : ObjectCallback<String> {
            override fun onError(msg: String?) {
                dialog.dismiss()
                Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
            }

            override fun onData(t: String?) {
                dialog.dismiss()
                Toast.makeText(context, "Your Order has benn added successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@CheckoutActivity, HomeActivityWithDrawer::class.java));
            }
        })
    }
}