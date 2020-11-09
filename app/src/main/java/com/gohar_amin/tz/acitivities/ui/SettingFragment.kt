package com.gohar_amin.tz.acitivities.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.gohar_amin.tz.R
import com.gohar_amin.tz.acitivities.HelpAndSupportActivity
import com.gohar_amin.tz.acitivities.LoginActivity
import com.gohar_amin.tz.utils.PrefHelper
import com.google.firebase.firestore.FirebaseFirestore


class SettingFragment : Fragment() {
    lateinit var tvLogout: TextView
    lateinit var helpAndSupport: TextView
    lateinit var EditProfile: TextView
    val USER_ID: String = "userId"
    lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_setting, container, false)
        tvLogout = root.findViewById(R.id.tvLogout)
        helpAndSupport = root.findViewById(R.id.helpAndSupport)
        EditProfile = root.findViewById(R.id.EditProfile)
        tvLogout.setOnClickListener {
            logout()
        }
        EditProfile.setOnClickListener {
            editName()
        }
        helpAndSupport.setOnClickListener {
            startActivity(Intent(context, HelpAndSupportActivity::class.java))
        }
        return root
    }

    private fun logout() {
        val pref = PrefHelper.getInstance(requireContext())
        pref!!.saveString(USER_ID, null)
        startActivity(Intent(context, LoginActivity::class.java))

    }

    private fun editName() {
        val factory = LayoutInflater.from(requireContext())
        val textEntryView: View = factory.inflate(R.layout.dailog_layout, null)
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
       dialogBuilder.setView(textEntryView)
        val alertDialog: AlertDialog = dialogBuilder.create()

        val etTitle: EditText = textEntryView.findViewById(R.id.etTitle)
        val btnEdit: Button = textEntryView.findViewById(R.id.btnEdit)
        btnEdit.setOnClickListener {
            if (!TextUtils.isEmpty(etTitle.text)) {

                val pref = PrefHelper.getInstance(requireContext())
                val id = pref!!.getString(USER_ID)
                FirebaseFirestore.getInstance().collection("users")
                    .document(id!!)
                    .update("name", etTitle.text.toString())
                    .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                root.findNavController().navigate(R.id.action_navigation_setting_to_navigation_home)
                                alertDialog.dismiss()
                                Toast.makeText(context, "Edit Successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "" + task.exception!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            } else {
                Toast.makeText(context, "Please enter the name", Toast.LENGTH_SHORT).show()
            }
        }
        alertDialog.show()
    }

}