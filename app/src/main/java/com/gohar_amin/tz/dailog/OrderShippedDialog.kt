package com.gohar_amin.tz.dailog

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.gohar_amin.tz.R
import com.gohar_amin.tz.callback.ObjectCallback
import com.gohar_amin.tz.model.Order
import com.gohar_amin.tz.model.UserGig
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.Utils
import com.google.firebase.firestore.FirebaseFirestore

class OrderShippedDialog(private var mContext: Context,private val orderId:String) : DialogFragment() {
    lateinit var pdilog:ProgressDialog
    var userId:String?=null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view=LayoutInflater.from(mContext).inflate(R.layout.shipped_item_layout,null)
        val tvProductName:TextView=view.findViewById(R.id.tvProductName)
        val textView:TextView=view.findViewById(R.id.tvCost)
        val imageView:ImageView=view.findViewById(R.id.imageView)

        val btnShipped:Button=view.findViewById(R.id.btnShipped)
        pdilog= ProgressDialog(mContext)
        pdilog.setTitle("Loading...")
        pdilog.show()
        val dailog=AlertDialog
                .Builder(mContext)
                .setView(view)
                .create();
        val collectionRef=FirebaseFirestore.getInstance().collection("orders")
        FirebaseHelper.getInstance()!!.getSingleFireStore(collectionRef, orderId, Order::class.java, object : ObjectCallback<Order> {
            override fun onError(msg: String?) {
                pdilog.dismiss()
                Toast.makeText(mContext, ""+msg, Toast.LENGTH_SHORT).show()
            }

            override fun onData(t: Order?) {
                t?.let {
                    pdilog.dismiss()
                    tvProductName.setText(it.purchasedGig!!.title)
                    textView.setText(""+it.purchasedGig!!.cost)
                    Utils.loadImage(mContext,it.purchasedGig!!.ImageUrl,imageView)
                }
            }
        })

        return dailog
    }
}