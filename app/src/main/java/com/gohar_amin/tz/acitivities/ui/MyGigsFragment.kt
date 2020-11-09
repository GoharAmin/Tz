package com.gohar_amin.tz.acitivities.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.adapters.GigsAdapter
import com.gohar_amin.tz.callback.ArrayCallback
import com.gohar_amin.tz.model.Gig
import com.gohar_amin.tz.model.UserGig
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.PrefHelper
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MyGigsFragment : Fragment() ,ArrayCallback<UserGig>{
    lateinit var recyclerView: RecyclerView
    lateinit var gigsAdapter: GigsAdapter
    val GIGS:String="gigs"
    lateinit var gigCollectionRef: CollectionReference
    lateinit var list:ArrayList<Gig>
    lateinit var firebaseHelper: FirebaseHelper
    val USER_ID:String="userId"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root= inflater.inflate(R.layout.fragment_my_gigs, container, false)

        recyclerView=root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager= GridLayoutManager(context,2);
        gigCollectionRef= FirebaseFirestore.getInstance().collection(GIGS)
        firebaseHelper= FirebaseHelper.getInstance()!!
        val id=PrefHelper.getInstance(requireContext())!!.getString(USER_ID)
        firebaseHelper.getSingleFireStoreByUid(
            gigCollectionRef,
            id!!,
            UserGig::class.java, this@MyGigsFragment)
        return root
    }
    override fun onError(msg: String?) {
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
    }

    override fun onData(list: MutableList<UserGig>?) {
        if(!list.isNullOrEmpty()) {
            gigsAdapter = GigsAdapter(requireContext(), list, true)
            recyclerView.adapter = gigsAdapter
        }
    }

}