package com.gohar_amin.tz.acitivities.ui.sell

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.gohar_amin.tz.R
import com.gohar_amin.tz.callback.ObjectCallback
import com.gohar_amin.tz.model.User
import com.gohar_amin.tz.model.UserGig
import com.gohar_amin.tz.utils.FirebaseHelper
import com.gohar_amin.tz.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
class SellFragment : Fragment() , AdapterView.OnItemSelectedListener ,ObjectCallback<String>{
    private var isAccepted: Boolean=false
    private var imageName: String?=null
    private var imageUrl: Uri?=null
    lateinit var spinner:Spinner
    lateinit var tvGigTitle:TextView
    lateinit var etTitle:EditText
    lateinit var tvDeliverTime:TextView
    lateinit var etDeliverTime:EditText
    lateinit var tvGigCost:TextView
    lateinit var etGigCost:EditText
    lateinit var tvGigDetails:TextView
    lateinit var etGigDetail:EditText
    lateinit var etAddPrice:EditText
    lateinit var etPerDay:EditText
    lateinit var tvPerDay:TextView
    lateinit var llAddtionals:LinearLayout
    lateinit var storageRef:StorageReference
    val GIGS:String="gigs"
    lateinit var tvBuyerRequired:TextView
    lateinit var tvAddMoreItems:TextView
    lateinit var rbOnSite:RadioButton
    lateinit var rbRemote:RadioButton
    lateinit var checkbox:CheckBox
    lateinit var etBuyerRequired:EditText
    lateinit var etGigDeliver:EditText
    lateinit var btnCreateGig:Button
    lateinit var ivEdit:ImageView
    lateinit var firebaseHelper: FirebaseHelper
    lateinit var gigCollectionRef:CollectionReference
    val SELECTED_IMAGE_NAME: String="selectedImageName"
    val SELECTED_IMAGE_PATH: String="selectedImagePath"
    val SELECT_IMAGE: String="selectImage"
    lateinit var user: UserGig
    lateinit var list: List<String>
    lateinit var ivProfile:ImageView
    lateinit var root:View
    lateinit var dialog:ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        initViews(root)
        initListeners()
        dialog= ProgressDialog(context)
        dialog.setCancelable(false);
        dialog.setTitle("Please wait ...")
        dialog.setMessage("Your gig is gonna create...")
        gigCollectionRef=FirebaseFirestore.getInstance().collection(GIGS)
        user= UserGig()
        user.id=System.currentTimeMillis().toString()
        user.uid=FirebaseAuth.getInstance().currentUser!!.uid
        storageRef=FirebaseStorage.getInstance().getReference("profiles")
        requireContext().registerReceiver(broadcastReceiver, IntentFilter(SELECT_IMAGE));
        return root
    }

    private fun initViews(root: View) {
        tvBuyerRequired = root.findViewById(R.id.tvBuyerRequired)
        tvDeliverTime = root.findViewById(R.id.tvDeliverTime)
        tvGigCost = root.findViewById(R.id.tvGigCost)
        llAddtionals = root.findViewById(R.id.llAddtionals)
        ivProfile = root.findViewById(R.id.ivProfile)
        tvGigDetails = root.findViewById(R.id.tvGigDetails)
        tvGigTitle = root.findViewById(R.id.tvGigTitle)
        etAddPrice = root.findViewById(R.id.etAddPrice)
        ivEdit = root.findViewById(R.id.ivEdit)
        tvPerDay = root.findViewById(R.id.tvPerDay)
        etBuyerRequired = root.findViewById(R.id.etBuyerRequired)
        rbOnSite = root.findViewById(R.id.rbOnSite)
        rbRemote = root.findViewById(R.id.rbRemote)
        checkbox = root.findViewById(R.id.checkbox)
        etGigDeliver = root.findViewById(R.id.etGigDeliver)
        etDeliverTime = root.findViewById(R.id.etDeliverTime)
        btnCreateGig = root.findViewById(R.id.btnCreateGig)
        etGigCost = root.findViewById(R.id.etGigCost)
        etGigDetail = root.findViewById(R.id.etGigDetail)
        etPerDay = root.findViewById(R.id.etPerDay)
        etTitle = root.findViewById(R.id.etTitle)
        tvAddMoreItems = root.findViewById(R.id.tvAddMoreItems)
        firebaseHelper = FirebaseHelper.getInstance()!!
        spinner = root
            .findViewById(R.id.spinner);
        list = listOf("Web Devolpment", "Mobile Devolpment", "Ios Devolpment","Seo","Digital Marketing")
        val adapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter)
        spinner.onItemSelectedListener=this@SellFragment
    }

    private fun createUserGig() {
        if(checkInput()){
            user.cost=(etGigCost.text.toString()).toInt()
            user.title=(etTitle.text.toString())
            user.deliverTime=(etDeliverTime.text.toString())
            user.detail=(etDeliverTime.text.toString())
            dialog.show()
            firebaseHelper.upload(
                context,
                storageRef,
                imageName,
                imageUrl,
                object : ObjectCallback<Uri> {
                    override fun onError(msg: String?) {
                        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()

                    }

                    override fun onData(t: Uri?) {
                        user.ImageUrl=t.toString()
                        dialog.dismiss()
            firebaseHelper.saveFireStoreDb(gigCollectionRef,user.id!!,user,this@SellFragment)
                    }
                })
        }
    }

    private fun checkInput(): Boolean {
        if(imageUrl==null || imageName==null){
            Toast.makeText(context, "Please Select an image", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(TextUtils.isEmpty(etGigCost.text)){
            Toast.makeText(context, "Please enter gig cost", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(TextUtils.isEmpty(etTitle.text)){
            Toast.makeText(context, "Please Enter gig title", Toast.LENGTH_SHORT).show()
            return false
        }

        else if(TextUtils.isEmpty(etDeliverTime.text)){
            Toast.makeText(context, "Please Enter Gig Time", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(TextUtils.isEmpty(etGigDetail.text)){
            Toast.makeText(context, "Please Enter Gig detail", Toast.LENGTH_SHORT).show()
            return false
        }else if(!isAccepted){
            Toast.makeText(context, "Please accept terms and conditions", Toast.LENGTH_SHORT).show()
            return false
        }
        else{
            if(TextUtils.isEmpty(etGigDeliver.text)){
                user.fastDeliverProduct=etGigDeliver.text.toString()
            }
            else if(TextUtils.isEmpty(etAddPrice.text)){
                user.fastDeliverPrice=(etAddPrice.text.toString())

            }
            else if(!TextUtils.isEmpty(etPerDay.text)){
                user.fastDeliverDays=(etPerDay.text.toString()).toInt()
            }
            else if(!TextUtils.isEmpty(etBuyerRequired.text)){
                user.requiredInfo=etBuyerRequired.text.toString()
            }
            return true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(broadcastReceiver)
    }
    private fun initListeners() {
        tvAddMoreItems.setOnClickListener {
            if(llAddtionals.visibility==View.VISIBLE){
                llAddtionals.visibility = View.GONE
            }else {
                llAddtionals.visibility = View.VISIBLE
            }
        }
        rbRemote.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                user.workOptions="Remote"
            }
        }
        rbOnSite.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                user.workOptions="OnSite"
            }
        }
        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            isAccepted=isChecked
        }
        ivEdit.setOnClickListener {
            Utils.showSelectedImages(context as AppCompatActivity )
        }
        etTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!=null && s.length>0){
                    tvGigTitle.visibility=View.VISIBLE
                }else{
                    tvGigTitle.visibility=View.INVISIBLE
                }
                Log.e("",""+s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        etBuyerRequired.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!=null && s.length>0){
                    tvBuyerRequired.visibility=View.VISIBLE
                }else{
                    tvBuyerRequired.visibility=View.INVISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        etDeliverTime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!=null && s.length>0){
                    tvDeliverTime.visibility=View.VISIBLE
                }else{
                    tvDeliverTime.visibility=View.INVISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        etGigCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!=null && s.length>0){
                    tvGigCost.visibility=View.VISIBLE
                }else{
                    tvGigCost.visibility=View.INVISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        etGigDetail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!=null && s.length>0){
                    tvGigDetails.visibility=View.VISIBLE
                }else{
                    tvGigDetails.visibility=View.INVISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        etPerDay.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!=null && s.length>0){
                    tvPerDay.visibility=View.VISIBLE
                }else{
                    tvPerDay.visibility=View.INVISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        btnCreateGig.setOnClickListener {
            createUserGig()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.e("itemSelected","parent");
        Log.e("itemSelected",""+list.get(position));
        user.workOptions=list.get(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
    val broadcastReceiver=object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!=null && intent.hasExtra(SELECTED_IMAGE_PATH)!=null){
                //imageUrl= Uri.fromFile(File(intent.getStringExtra(SELECTED_IMAGE_PATH)));
                user.ImageUrl= intent.getStringExtra(SELECTED_IMAGE_PATH)
                imageUrl= Uri.parse(intent.getStringExtra(SELECTED_IMAGE_PATH))
                imageName=intent.getStringExtra(SELECTED_IMAGE_NAME);
                //Utils.loadImage(context!!,imageUrl.toString(), ivProfile);
                Utils.loadImage(context!!,user.ImageUrl, ivProfile);

                //user.ImageUrl= imageUrl!!.toString()
            }
        }
    }

    override fun onError(msg: String?) {
        dialog.dismiss()
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
    }

    override fun onData(t: String?) {
        dialog.dismiss()
        Toast.makeText(context, "Your gig Successfully Created", Toast.LENGTH_SHORT).show()
        root.findNavController().navigate(R.id.action_navigation_sell_to_navigation_home)
    }
}