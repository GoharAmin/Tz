package com.gohar_amin.tz.acitivities.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.adapters.AllUsersAdapter
import com.gohar_amin.tz.callback.ArrayCallback
import com.gohar_amin.tz.callback.ObjectCallback
import com.gohar_amin.tz.model.GeneralModel
import com.gohar_amin.tz.model.User
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.JsonParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class AllUsersFragment : Fragment() ,ArrayCallback<User>{
    lateinit var allUsersAdapter: AllUsersAdapter
lateinit var collectionRef:CollectionReference
    lateinit var firebaseHelper: FirebaseHelper
    lateinit var root: View
lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_all_users, container, false)
        recyclerView=root.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        firebaseHelper= FirebaseHelper.getInstance()!!
        val uid=FirebaseAuth.getInstance().currentUser!!.uid
        collectionRef= FirebaseFirestore.getInstance().collection("chatUsers").document(uid).collection("msg")
        firebaseHelper.getFireStore(collectionRef,User::class.java,this@AllUsersFragment)
        return root
    }

    override fun onError(msg: String?) {
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
    }

    override fun onData(list: MutableList<User>?) {
        list?.let {
            allUsersAdapter = AllUsersAdapter(it, requireContext(),root.findNavController())
            recyclerView.adapter=allUsersAdapter
        }
    }
}