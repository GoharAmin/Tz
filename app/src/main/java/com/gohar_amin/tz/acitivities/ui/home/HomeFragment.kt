package com.gohar_amin.tz.acitivities.ui.home

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.adapters.CatagoriesAdapter
import com.gohar_amin.tz.adapters.GigsAdapter
import com.gohar_amin.tz.callback.ArrayCallback
import com.gohar_amin.tz.callback.ObjectCallback
import com.gohar_amin.tz.model.*
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.JsonParser
import com.gohar_amin.tz.utils.PrefHelper
import com.gohar_amin.tz.utils.Utils
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() ,ArrayCallback<UserGig>{
    lateinit var deliveredRef: CollectionReference
    lateinit var  popularGigsAdapter:GigsAdapter
    lateinit var  latestGigsAdapter:GigsAdapter
    lateinit var  categoryAdapter:CatagoriesAdapter
    lateinit var searchViewa:SearchView
    lateinit var rvCategies:RecyclerView
    lateinit var rvLatestGigs:RecyclerView
    lateinit var rvPopularGigs:RecyclerView
    lateinit var categoryList:ArrayList<Category>
    lateinit var populrGigList:ArrayList<UserGig>
    lateinit var latestGigList:ArrayList<UserGig>
    lateinit var firebaseHelper: FirebaseHelper
    lateinit var tvViewAll2:TextView
    lateinit var tvViewAll1:TextView
    val GIGS:String="gigs"
    lateinit var gigCollectionRef:CollectionReference
    lateinit var searchGigRef:CollectionReference

        lateinit var root: View
    lateinit var ivSearch:ImageView
        var userId: String?=null
    lateinit var etSerach:EditText
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        ivSearch=root.findViewById(R.id.ivSearch)
        etSerach=root.findViewById(R.id.etSerach)
        searchViewa=root.findViewById(R.id.searchViewa)
        tvViewAll1=root.findViewById(R.id.tvViewAll1)
        tvViewAll2=root.findViewById(R.id.tvViewAll2)
        rvCategies=root.findViewById(R.id.rvTopCatagories)
        rvLatestGigs=root.findViewById(R.id.rvLatestGigs)
        rvPopularGigs=root.findViewById(R.id.rvPopularGigs)

        rvPopularGigs.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        rvCategies.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        rvLatestGigs.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        rvCategies.setHasFixedSize(true)
        rvLatestGigs.setHasFixedSize(true)
        rvPopularGigs.setHasFixedSize(true)
        firebaseHelper= FirebaseHelper.getInstance()!!
        gigCollectionRef=FirebaseFirestore.getInstance().collection(GIGS)
        searchGigRef=FirebaseFirestore.getInstance().collection(GIGS)
        deliveredRef= FirebaseFirestore.getInstance().collection("deliveredOrder");

        userId = PrefHelper.getInstance(requireContext())!!.getString("userId")
        firebaseHelper.getFireStore(
            gigCollectionRef,
            UserGig::class.java,
            userId!!,
            "purchased",
            10,
            object : ArrayCallback<UserGig> {
                override fun onError(msg: String?) {
                    Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
                }

                override fun onData(list: MutableList<UserGig>?) {
                    if(list!=null && !list.isEmpty()){
                        popularGigsAdapter= GigsAdapter(requireContext(),list,false)
                        rvPopularGigs.adapter=popularGigsAdapter
                    }
                }
            })
        firebaseHelper.getFireStore(
            gigCollectionRef,
            UserGig::class.java,
            userId!!,
            "createdAt",
            10,
            object : ArrayCallback<UserGig> {
                override fun onError(msg: String?) {
                    Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
                }

                override fun onData(list: MutableList<UserGig>?) {
                    if(!list.isNullOrEmpty()) {
                        latestGigsAdapter = GigsAdapter(requireContext(), list, false)
                        rvLatestGigs.adapter = latestGigsAdapter
                    }
                }
            })

        setUpForLists()
        tvViewAll1.setOnClickListener {
            root.findNavController().navigate(R.id.action_navigation_home_to_navigation_buy)
        }
        tvViewAll2.setOnClickListener {
            root.findNavController().navigate(R.id.action_navigation_home_to_navigation_buy)
        }
        categoryAdapter= CatagoriesAdapter(requireContext(),categoryList)
        rvCategies.adapter=categoryAdapter
        /*if(etSerach.isFocused){
            ivSearch.visibility=View.GONE
        }else{
            ivSearch.visibility=View.VISIBLE
        }*/
        ivSearch.setOnClickListener {
            if(!TextUtils.isEmpty(etSerach.text)){
                searchGig()
            }else{
                Toast.makeText(context, "Please enter the email for search", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        getOrder()
    }
    private fun searchGig() {
        Utils.showDialog(context)
        firebaseHelper.getSingleFireStoreBytitle(searchGigRef, "title", etSerach.text.toString(), UserGig::class.java, object : ObjectCallback<UserGig> {
            override fun onError(msg: String?) {
                Utils.dismissDialog()
                Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
            }

            override fun onData(t: UserGig?) {
                if(t!=null) {
                    Utils.dismissDialog()
                    val b = Bundle()
                    b.putString("searchedGig",JsonParser.toJson(t));
                    root.findNavController().navigate(R.id.action_navigation_home_to_navigation_buy,b)
                }
            }
        })
    }

    private fun setUpForLists() {
        categoryList= ArrayList()
        populrGigList=ArrayList()
        latestGigList=ArrayList()
        categoryList.add(Category("Web Devolpment"))
        categoryList.add(Category("Mobile Devolpment"))
        categoryList.add(Category("Ios Devolpment"))
        categoryList.add(Category("Seo"))
        categoryList.add(Category("Digital Marketing"))
    }

    override fun onError(msg: String?) {
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
    }

    override fun onData(list: MutableList<UserGig>?) {
        if(!list.isNullOrEmpty()){

        }
    }
    private fun getOrder() {
        FirebaseHelper
                .getInstance()!!
                .getSingleFireStore(deliveredRef, userId!!, DeliveredOrder::class.java, object : ObjectCallback<DeliveredOrder> {
                    override fun onError(msg: String?) {
                        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onData(t: DeliveredOrder?) {
                        t?.let {
                            if(t.isDelivered && !t.isReview) {
                                showDialog(it)
                            }
                        }
                    }
                })
    }
    private fun showDialog(order:DeliveredOrder){
        val view= LayoutInflater.from(context).inflate(R.layout.order_accept_layout,null)
        val textView: TextView =view.findViewById(R.id.textView2)
        val btnAccept: Button =view.findViewById(R.id.btnAccept)
        val btnCancel: Button =view.findViewById(R.id.btnCancel)
        textView.setText(order.name)
        val dialog:AlertDialog=AlertDialog
                .Builder(requireContext())
                .setView(view)
                .create()
                dialog.show()
        btnAccept.setOnClickListener {
            order.isAccepted=true
            deliveredRef.document(order.id!!).set(order)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context, "Order is Accepted", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            if(order.isAccepted && !order.isReview){
                                getReview(order)
                            }
                        }else{
                            dialog.dismiss()
                            Toast.makeText(context, ""+it.exception!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
        }
        btnCancel.setOnClickListener {
            deliveredRef.document(order.id!!).delete().addOnCompleteListener {
                if(it.isSuccessful){
                    dialog.dismiss()
                    Toast.makeText(context, "Order ha been Rejected", Toast.LENGTH_SHORT).show()
                }else{
                    dialog.dismiss()
                    Toast.makeText(context, ""+it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun getReview(order: DeliveredOrder) {
        val view= LayoutInflater.from(context).inflate(R.layout.review_layout,null)
        val ratingBar: RatingBar =view.findViewById(R.id.ratingBar)
        val btnSubmit: Button =view.findViewById(R.id.btnSubmit)
        btnSubmit.setText(order.name)
        val dialog:AlertDialog=AlertDialog
                .Builder(requireContext())
                .setView(view)
                .create()
        val rating = ratingBar.stepSize
        btnSubmit.setOnClickListener {
            if(rating>0) {
                Utils.showDialog(context)
                order.isReview = true
                firebaseHelper.saveFireStoreDb(deliveredRef, order.id!!, order, object : ObjectCallback<String> {
                    override fun onError(msg: String?) {
                        Utils.dismissDialog()
                        Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onData(t: String?) {
                        firebaseHelper.getSingleByUid(gigCollectionRef, order.seller!!, UserGig::class.java, object : ObjectCallback<UserGig> {
                            override fun onError(msg: String?) {
                                Utils.dismissDialog()
                                Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
                            }

                            override fun onData(t: UserGig?) {
                                t?.let {
                                    it.rating=(it.rating+rating)
                                    it.raters=(it.raters+1)
                                    updateGig(it)
                                }

                            }
                        })
                    }
                })
            }else{
                Toast.makeText(context, "Please Rate us", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun updateGig(it: UserGig) {
        firebaseHelper.saveFireStoreDb(gigCollectionRef, it.id!!, it, object : ObjectCallback<String> {
            override fun onError(msg: String?) {
                Utils.dismissDialog()
                Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
            }

            override fun onData(t: String?) {
                Utils.dismissDialog()
                Toast.makeText(context, "Successfully rating is submitted", Toast.LENGTH_SHORT).show()
            }
        })
    }

}