package com.gohar_amin.tz.acitivities.ui.buy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.adapters.GigsAdapter
import com.gohar_amin.tz.callback.ArrayCallback
import com.gohar_amin.tz.model.Gig
import com.gohar_amin.tz.model.UserGig
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.JsonParser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class BuyFragment : Fragment() {
lateinit var recyclerView:RecyclerView
lateinit var gigsAdapter: GigsAdapter
    val GIGS:String="gigs"
    lateinit var gigCollectionRef: CollectionReference
    lateinit var list:ArrayList<Gig>
    lateinit var firebaseHelper: FirebaseHelper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        recyclerView=root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager=GridLayoutManager(context,2);

        if(arguments!=null){
            if(requireArguments().containsKey("searchedGig")){
                val searchGig=requireArguments().getString("searchedGig")
                val userGig:UserGig?=JsonParser.toObject(searchGig,UserGig::class.java)
                val list= mutableListOf<UserGig>(userGig!!)
                setData(list)
                Log.e("searchedData",""+searchGig);
            }
        }else{
            Log.e("searchedData","else ");
            gigCollectionRef= FirebaseFirestore.getInstance().collection(GIGS)
        firebaseHelper= FirebaseHelper.getInstance()!!
        firebaseHelper.getFireStore(
            gigCollectionRef,
            UserGig::class.java,
            object : ArrayCallback<UserGig> {
                override fun onError(msg: String?) {
                    Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
                }

                override fun onData(list: MutableList<UserGig>?) {
                    if(!list.isNullOrEmpty()) {
                        setData(list)
                    }
                }
            })
        }

        return root
    }

    private fun setData(list: MutableList<UserGig>?) {
        gigsAdapter = GigsAdapter(requireContext(), list!!, true)
        recyclerView.adapter = gigsAdapter
    }

    private fun setUpList() {
        list=ArrayList();
        val gig1=Gig()
        gig1.name="xyz"
        gig1.price=12
        val gig2=Gig()
        gig2.name="xyz"
        gig2.price=12
        val gig3=Gig()
        gig3.name="xyz"
        gig3.price=12
        val gig4=Gig()
        gig4.name="xyz"
        gig4.price=12
        val gig5=Gig()
        gig5.name="xyz"
        gig5.price=12
        val gig6=Gig()
        gig6.name="xyz"
        gig6.price=12
        val gig7=Gig()
        gig7.name="xyz"
        gig7.price=12
        val gig8=Gig()
        gig8.name="xyz"
        gig8.price=12
        val gig9=Gig()
        gig9.name="xyz"
        gig9.price=12
        list.add(gig1)
        list.add(gig2)
        list.add(gig3)
        list.add(gig4)
        list.add(gig5)
        list.add(gig6)
        list.add(gig7)
        list.add(gig8)
        list.add(gig9)
    }
}