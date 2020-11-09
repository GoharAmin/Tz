package com.gohar_amin.tz.acitivities.ui.home

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
import com.gohar_amin.tz.model.Category
import com.gohar_amin.tz.model.Gig
import com.gohar_amin.tz.model.User
import com.gohar_amin.tz.model.UserGig
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.JsonParser
import com.gohar_amin.tz.utils.PrefHelper
import com.gohar_amin.tz.utils.Utils
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() ,ArrayCallback<UserGig>{
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
        val userId=PrefHelper.getInstance(requireContext())!!.getString("userId")
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
}