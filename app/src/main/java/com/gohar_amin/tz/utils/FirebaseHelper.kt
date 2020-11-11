package com.gohar_amin.tz.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.gohar_amin.tz.callback.ArrayCallback
import com.gohar_amin.tz.callback.BaseCallback
import com.gohar_amin.tz.callback.ObjectCallback
import com.gohar_amin.tz.model.UserGig
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class FirebaseHelper {
    fun <T> upload(
        context: Context?,
        reference: StorageReference,
        imageName: String?,
        imageUrl: Uri?,
        callback: ObjectCallback<T>
    ) {
        Log.e(TAG, "image Url -> $imageUrl")
        Log.e(TAG, "image Name -> $imageName")
        if (isInternetAvailable(callback)) {
            if (imageName != null && imageUrl != null) {
                val upload = reference.child(imageName + System.currentTimeMillis())
                upload.putFile(imageUrl)
                    .addOnCompleteListener { task: Task<UploadTask.TaskSnapshot?> ->
                        if (task.isSuccessful) {
                            upload.downloadUrl
                                .addOnCompleteListener { task1: Task<Uri?> ->
                                    if (task1.isSuccessful) {
                                        callback.onData(task1.result as T)
                                    } else {
                                        Log.e(
                                            TAG,
                                            "download url ->" + task.exception!!.message
                                        )
                                        callback.onError("Please try again...")
                                    }
                                }
                        } else {
                            setErrorMessage(callback, task)
                        }
                    }
            } else {
                Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*fun <T> saveRealtimeDb(
        databaseReference: DatabaseReference,
        `object`: Any?,
        callback: ObjectCallback<T>
    ) {
        if (isInternetAvailable(callback)) {
            databaseReference.setValue(`object`).addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    callback.onData("Congratulations..." as T)
                } else {
                    setErrorMessage<T>(callback, task)
                }
            }
        }
    }*/

    private fun <T> setErrorMessage(callback: BaseCallback, task: Task<T>) {
        try {
            callback.onError("" + task.exception!!.message)
        } catch (ex: NullPointerException) {
            callback.onError(ex.message)
        }
    }

    fun saveFireStoreDb(
        reference: CollectionReference,
        path: String,
        obj: Any,
        callback: ObjectCallback<String>
    ) {
        if (isInternetAvailable(callback)) {
            reference.document(path).set(obj).addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    callback.onData("Conragulations...")
                } else {
                    setErrorMessage(callback, task)
                }
            }
        }
    }

    fun <T> getFireStore(
        collectionReference: CollectionReference,
        clazz: Class<T>,
        userId:String,
        orderBy:String,
        limit:Long,
        callback: ArrayCallback<T>
    ) {
        if (isInternetAvailable(callback)) {
            collectionReference.orderBy(orderBy, Query.Direction.DESCENDING).limit(limit).get().addOnCompleteListener { task: Task<QuerySnapshot?> ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val list: MutableList<T> = ArrayList()
                        if (task.result!!.documents != null) {
                            for (obj in task.result!!.documents) {
                                val o=obj.toObject(UserGig::class.java)
                                if(!userId.equals(o!!.uid)) {
                                    if (obj != null) list.add(obj.toObject(clazz) as T)
                                }
                            }
                            callback.onData(list)
                        }
                    } else {
                        Log.e("", "this collection has not documents...")
                    }
                } else {
                    setErrorMessage(callback, task)
                }
            }
        }
    }fun <T> getFireStore(
        collectionReference: CollectionReference,
        clazz: Class<T>,
        callback: ArrayCallback<T>
    ) {
        if (isInternetAvailable(callback)) {
            collectionReference.get().addOnCompleteListener { task: Task<QuerySnapshot?> ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val list: ArrayList<T> = ArrayList()
                        if (task.result!!.documents != null) {

                            for (obj in task.result!!.documents) {
                                if (obj != null) list.add(obj.toObject(clazz) as T)
                            }
                            callback.onData(list)
                        }
                    } else {
                        Log.e("", "this collection has not documents...")
                    }
                } else {
                    setErrorMessage(callback, task)
                }
            }
        }
    }

    fun <T> getSingleFireStore(
        collectionReference: CollectionReference,
        path: String?,
        clazz: Class<T>,
        callback: ObjectCallback<T>
    ) {
        if (isInternetAvailable(callback)) {
            collectionReference.document(path!!).get()
                .addOnCompleteListener { task: Task<DocumentSnapshot?> ->
                    if (task.isSuccessful) {
                        if (task.result != null) {
                            callback.onData(task.result!!.toObject(clazz)as T)
                        } else {
                            Log.e(
                                TAG,
                                "task.getResult() returns null"
                            )
                        }
                    } else {
                        setErrorMessage(callback, task)
                    }
                }
        }
    }


    fun <T> getSingleFireStoreBytitle(
            collectionRef: CollectionReference,
            key: String,
            value:String,
            clazz: Class<T>,
            callback: ObjectCallback<T>
    ) {
        if (isInternetAvailable(callback)) {
            collectionRef.whereEqualTo(key,value).get()
                    .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                        if (task.isSuccessful) {
                            if (task.result != null && !task.result!!.documents.isEmpty()) {
                                callback.onData(
                                        task.result!!.documents[0].toObject(clazz) as T
                                )
                            } else {
                                callback.onError("Your searched gig is not available")
                            }
                        } else {
                            setErrorMessage(callback, task)
                        }
                    }
        }
    }





    fun <T> getSingleFireStoreByUid(
        collectionRef: CollectionReference,
        uid: String,
        clazz: Class<T>,
        callback: ObjectCallback<T>
    ) {
        if (isInternetAvailable(callback)) {
            collectionRef.whereEqualTo("id",uid).get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        if (task.result != null && !task.result!!.documents.isEmpty()) {
                            callback.onData(
                                task.result!!.documents[0].toObject(clazz) as T
                            )
                        } else {
                            Log.e(
                                TAG,
                                "task.getResult() returns null"
                            )
                        }
                    } else {
                        setErrorMessage(callback, task)
                    }
                }
        }
    }

    fun <T> getSingleByUid(
            collectionRef: CollectionReference,
            uid: String,
            clazz: Class<T>,
            callback: ObjectCallback<T>
    ) {
        if (isInternetAvailable(callback)) {
            collectionRef.whereEqualTo("uid",uid).get()
                    .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                        if (task.isSuccessful) {
                            if (task.result != null && !task.result!!.documents.isEmpty()) {
                                callback.onData(
                                        task.result!!.documents[0].toObject(clazz) as T
                                )
                            } else {
                                Log.e(
                                        TAG,
                                        "task.getResult() returns null"
                                )
                            }
                        } else {
                            setErrorMessage(callback, task)
                        }
                    }
        }
    }


    fun <T> getSingleFireStoreByUid(
        collectionRef: CollectionReference,
        uid: String,
        clazz: Class<T>,
        callback: ArrayCallback<T>
    ) {
        if (isInternetAvailable(callback)) {
            collectionRef.whereEqualTo("uid",uid).get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        if (task.result != null && !task.result!!.documents.isEmpty()) {
                            val list: ArrayList<T> = ArrayList()
                                for (obj in task.result!!.documents) {
                                    if (obj != null) list.add(obj.toObject(clazz) as T)
                                }
                                callback.onData(list)
                        } else {
                            Log.e(
                                TAG,
                                "task.getResult() returns null"
                            )
                        }
                    } else {
                        setErrorMessage(callback, task)
                    }
                }
        }
    }

/*    fun <T> getRealtimeDb(
        databaseReference: DatabaseReference,
        clazz: Class<*>,
        callback: ArrayCallback<T>
    ) {
        if (isInternetAvailable(callback)) {
            databaseReference.addChildEventListener(object : ChildEventListener() {
                fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    getChildren(snapshot, clazz, callback)
                }

                fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    getChildren(snapshot, clazz, callback)
                }

                fun onChildRemoved(snapshot: DataSnapshot) {}
                fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "error details ->" + error.getDetails())
                    Log.e(TAG, "error message ->" + error.getMessage())
                    callback.onError(error.getMessage())
                }
            })
        }
    }*/

  /*  private fun <T> getChildren(
        snapshot: DataSnapshot,
        clazz: Class<*>,
        callback: ArrayCallback<T>
    ) {
        if (isInternetAvailable(callback)) {
            if (snapshot.exists()) {
                val list: MutableList<T> = ArrayList()
                for (dataSnapshot in snapshot.getChildren()) {
                    list.add(dataSnapshot.getValue(clazz) as T)
                }
                callback.onData(list)
            } else {
                Log.e(TAG, "This database reference does not exists ")
            }
        }
    }
*/
    fun deleteFireStore(
        collectionReference: CollectionReference,
        path: String?,
        callback: ObjectCallback<String?>
    ) {
        if (isInternetAvailable(callback)) {
            if (path != null) {
                collectionReference.document(path).delete()
                    .addOnCompleteListener { task: Task<Void?> ->
                        if (task.isSuccessful) {
                            callback.onData(path)
                        } else {
                            setErrorMessage(callback, task)
                        }
                    }
            }
        }
    }

  /*  fun <T> deleteRealtimeDb(
        databaseReference: DatabaseReference,
        callback: ObjectCallback<T>,
        msg: String
    ) {
        if (isInternetAvailable(callback)) {
            databaseReference.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    callback.onData(msg as T)
                } else {
                    setErrorMessage<T>(callback, task)
                }
            }
        }
    }
*/
    fun <T> emailAndPasswordCreate(
        email: String,
        password: String,
        callback: ObjectCallback<T>
    ) {
        if (isInternetAvailable(callback)) {
            firebaseAuth.createUserWithEmailAndPassword(
                email!!,
                password!!
            ).addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    callback.onData(firebaseAuth.currentUser as T)
                } else {
                    setErrorMessage(callback, task)
                }
            }
        }
    }

    fun <T> emailAndPasswordLogin(email: String?, password: String?, callback: ObjectCallback<T>) {
        if (isInternetAvailable(callback)) {
            Companion.firebaseAuth.signInWithEmailAndPassword(
                email!!,
                password!!
            ).addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    callback.onData(task.result!!.user as T)
                } else {
                    setErrorMessage(callback, task)
                }
            }
        }
    }

    fun <T> logout(context: Context, prefHelper: PrefHelper, key: String?) {
        Companion.firebaseAuth.signOut()
        prefHelper.saveString(key, null)
        //context.startActivity(Intent(context, WelcomeActivity::class.java))
    }

    val currentUserUid: String
        get() = Companion.firebaseAuth.currentUser!!.uid
    val currentUser: FirebaseUser?
        get() = Companion.firebaseAuth.currentUser
    val firebaseAuth: FirebaseAuth
        get() = Companion.firebaseAuth

    fun isInternetAvailable(callback: BaseCallback): Boolean {
        if (!MyApplication.isConnected) {
            callback.onError("Please connect your Internet")
        }
        Log.e("isConnected", "intenet connection -> " + MyApplication.isConnected)
        return MyApplication.isConnected
    }

    companion object {
        const val TAG = "FirebaseHelper"
        var _this: FirebaseHelper? = null
        lateinit var firebaseAuth: FirebaseAuth
        fun getInstance():FirebaseHelper?{
            if(_this==null){
                _this = FirebaseHelper()
                firebaseAuth = FirebaseAuth.getInstance()
            }
            return _this
        }
    }
}