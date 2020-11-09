package com.gohar_amin.tz.acitivities.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.adapters.MessageAdapter
import com.gohar_amin.tz.model.Chat
import com.gohar_amin.tz.model.GeneralModel
import com.gohar_amin.tz.model.User
import com.gohar_amin.tz.utils.JsonParser
import com.gohar_amin.tz.utils.PrefHelper
import com.gohar_amin.tz.utils.Utils
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ChatActivity : AppCompatActivity() {
    private var senderUser: User?=null
    private var user: User? = null
    lateinit var fabSend: FloatingActionButton
    lateinit var etTextMessage: EditText
    var prefHelper: PrefHelper? = null
    var receiverId: String? = null
    var chatList: ArrayList<Chat>? = null
    var messageAdapter: MessageAdapter? = null
    var recyclerview: RecyclerView? = null
    var firebaseUser: FirebaseUser? = null
    private var senderId: String? = null
    lateinit var chatReference: CollectionReference
    lateinit var chatUsers: CollectionReference
    lateinit var receiveReference: CollectionReference
    lateinit var firebaseStorage: StorageReference
    private val TAG = "ChatActivity"
    private var allUserlist: ArrayList<User>? = null
    private var reciverUserlist: ArrayList<User>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_chat)
        fabSend = findViewById(R.id.fabSend)
        etTextMessage = findViewById(R.id.etTextMessage)
        recyclerview = findViewById(R.id.recyclerview)
        recyclerview!!.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerview!!.setHasFixedSize(true)
        receiverId = intent.getStringExtra("openChat")
        val usersExtra = intent.getStringExtra("allUsers");
        val userExtra = intent.getStringExtra("recevierUser");
        if (userExtra != null) {
            user = JsonParser.toObject(userExtra, User::class.java)
        }
        firebaseUser = FirebaseAuth.getInstance().currentUser
        chatUsers = FirebaseFirestore.getInstance().collection("chatUsers")
        prefHelper = PrefHelper.getInstance(this)
        senderId = prefHelper!!.getString("userId")
        val me=prefHelper!!.getString("me")
        senderUser=JsonParser.toObject(me,User::class.java)

        if (senderId == null) {
            senderId = firebaseUser!!.uid
        }
        if (receiverId != null) {
            validateChat()
        }
        /*else if(prefHelper!!.getString("recevierId")!=null){
            validateChat()
        }*/ /*else {

        }*/

        /*if (usersExtra == null) {
            Log.e("userExtra", "usersExtra==null")
            getAllUsers()
        } else {
            allUserlist = JsonParser.toList(usersExtra, User::class.java)
        }*/
        fabSend.setOnClickListener { v: View? ->
            if (receiverId != null)
                sendMessage()
        }

    }

    private fun getAllUsers() {
        chatUsers.document(senderId!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result != null) {
                    val res: GeneralModel? = task.result!!.toObject(GeneralModel::class.java)
                    if (res != null) {
                        Log.e("userExtra", "task !=null")
                        allUserlist = JsonParser.toList(res.users, User::class.java)
                    }
                    Utils.dismissDialog()
                }
            } else {
                Log.e("userExtra", "" + task.exception!!.message)
                Utils.dismissDialog()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
        chatUsers.document(receiverId!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result != null) {
                    val res: GeneralModel? = task.result!!.toObject(GeneralModel::class.java)
                    if (res != null) {
                        Log.e("userExtra", "task !=null")
                        reciverUserlist = JsonParser.toList(res.users, User::class.java)
                    }
                    Utils.dismissDialog()
                }
            } else {
                Log.e("userExtra", "" + task.exception!!.message)
                Utils.dismissDialog()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateChat() {
        chatReference =
                FirebaseFirestore.getInstance().collection("chat").document(senderId!!)
                        .collection(receiverId!!)
        receiveReference =
                FirebaseFirestore.getInstance().collection("chat").document(receiverId!!)
                        .collection(senderId!!)
        Utils.dismissDialog()
        firebaseStorage = FirebaseStorage.getInstance().getReference("chat")
        chatList = ArrayList()
        messageAdapter = MessageAdapter(chatList!!, this)
        recyclerview!!.adapter = messageAdapter
        loadData()
    }

    private fun sendMessage() {
        val chat = Chat(senderId, receiverId, "" + System.currentTimeMillis(), Utils.TEXT_MESSAGE)
        chat.messageTypeId = Utils.TEXT_MESSAGE
        chat.userName = firebaseUser!!.email
        chat.text = etTextMessage.text.toString()
        etTextMessage.setText("")
        send(chat)
    }

    private fun send(chat: Chat) {
        val time = "" + System.currentTimeMillis()
            Utils.showDialog(this)
            val model = GeneralModel()
            model.users = JsonParser.toJson(allUserlist)
            chatUsers.document(receiverId!!).collection("msg").document(senderId!!).set(senderUser!!)
                    .addOnCompleteListener { t ->
                        if (t.isSuccessful) {
                            val model1 = GeneralModel()
                            model1.users = JsonParser.toJson(reciverUserlist)
                            chatUsers.document(senderId!!).collection("msg").document(receiverId!!).set(user!!)
                                    .addOnCompleteListener { tk ->
                                        if (tk.isSuccessful) {
                                            canSent(time, chat)

                                        } else {
                                            Utils.dismissDialog()
                                            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                        } else {
                            Utils.dismissDialog()
                            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
                        }
                    }

    }

    private fun canSent(time: String, chat: Chat) {
        chatReference.document(time).set(chat).addOnCompleteListener { task: Task<Void> ->
            this.sendMessage(
                    task
            )
        }
        receiveReference.document(time).set(chat).addOnCompleteListener { task: Task<Void> ->
            this.sendMessage(
                    task
            )
        }
    }

    private fun sendMessage(task: Task<Void>) {
        if (task.isSuccessful) {
            loadData()
        } else {
            Utils.dismissDialog()
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {

        chatReference.addSnapshotListener{query,e->
            if(query!=null && !query.isEmpty){
                chatList!!.clear()
                for (documentSnapshot in query) {
                    val c = documentSnapshot.toObject(Chat::class.java)
                    c.isReceiver = isReceiver(c.senderId, senderId!!)
                    chatList!!.add(c)
                    messageAdapter!!.notifyDataSetChanged()
                }
                Utils.dismissDialog()
            }
        }
        chatReference.get().addOnCompleteListener { task: Task<QuerySnapshot?> ->
            getLoadData(
                    task
            )
        }
        //receiveReference.get().addOnCompleteListener(this::getLoadData);
    }

    private fun getLoadData(task: Task<QuerySnapshot?>) {
        if (task.isSuccessful) {
            if (task.result != null) {
                chatList!!.clear()
                for (documentSnapshot in task.result!!.documents) {
                    val c = documentSnapshot.toObject(Chat::class.java)
                    c!!.isReceiver = isReceiver(c.senderId, senderId!!)
                    chatList!!.add(c)
                    messageAdapter!!.notifyDataSetChanged()
                }
                Utils.dismissDialog()
            }
        } else {
            Utils.dismissDialog()
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isReceiver(receiverId: String?, senderId: String): Int {
        return if (receiverId != senderId) {
            Utils.RECEIVER_MESSAGE
        } else {
            Utils.SENDER_MESSAGE
        }
    }


    /*private fun uploadImages(data: Intent) {
        val images = data.getStringExtra(CustomConstants.SELECTED_IMAGE)
        imageList = JsonParser.toList(images, Image::class.java)
        for (image in imageList) {
            upload(image)
        }
    }*/

    /*private fun upload(image: Image) {
        Utils.showDialog(context)
        Log.e(TAG, "filepath -> " + image.getPath())
        val child =
            firebaseStorage.child(receiverId!!).child(image.getName() + System.currentTimeMillis())
        child.putFile(Uri.fromFile(File(image.getPath())))
            .addOnCompleteListener { task: Task<UploadTask.TaskSnapshot?> ->
                if (task.isSuccessful) {
                    Utils.dismissDialog()
                    saveImageReference(child)
                } else {
                    Utils.dismissDialog()
                    Log.e(TAG, "upload")
                    Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show()
                }
            }
    }*/

    /*private fun saveImageReference(child: StorageReference) {
        child.downloadUrl.addOnCompleteListener { task: Task<Uri> ->
            if (task.isSuccessful) {
                val chat = Chat(
                    senderId,
                    receiverId,
                    "" + System.currentTimeMillis(),
                    Utils.IMAGE_MESSAGE
                )
                chat.imageUrl = "" + task.result
                chat.userName = firebaseUser!!.phoneNumber
                send(chat)
            } else {
                Log.e(TAG, "url ->saveImageReference" + task.exception!!.message)
                Toast.makeText(context, "Please try Again", Toast.LENGTH_SHORT).show()
            }
        }
    }*/
}