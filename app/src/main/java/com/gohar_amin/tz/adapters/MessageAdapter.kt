package com.gohar_amin.tz.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.callback.ActionCallback
import com.gohar_amin.tz.dailog.OrderShippedDialog
import com.gohar_amin.tz.model.Chat
import com.gohar_amin.tz.utils.Utils
import com.google.firebase.auth.FirebaseAuth


class MessageAdapter(var list: MutableList<Chat>, context: Context,private val callBack:ActionCallback) :
    RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {
    var context: Context
    var id:String
    init {
        id=FirebaseAuth.getInstance().currentUser!!.uid
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View
        when (viewType) {
            Utils.RECEIVER_MESSAGE -> view = LayoutInflater.from(context)
                .inflate(R.layout.receiver_message_item_layout, parent, false)
            else -> view = LayoutInflater.from(context)
                .inflate(R.layout.sender_message_itm_layout, parent, false)
        }
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val chat = list[position]
        holder.tvName.text = chat.userName
        if (chat.messageTypeId == Utils.TEXT_MESSAGE) {
            holder.ivMedia.setVisibility(View.GONE)
            holder.tvText.visibility = View.VISIBLE
            holder.tvText.text = chat.text
        } else {
            holder.ivMedia.setVisibility(View.VISIBLE)
            holder.tvText.visibility = View.GONE
            Utils.loadImage(context, chat.imageUrl, holder.ivMedia)
        }

        holder.root.setOnClickListener {
            Log.e("onSuccess"," "+chat.clickable);
            Log.e("onSuccess"," "+id);
            if(chat.clickable!=null && chat.clickable.equals(id)){
                callBack.onSuccess()
                Log.e("onSuccess","click in adapter");
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(chatList: List<Chat>) {
        val prev = list.size
        list.addAll(chatList)
        notifyItemRangeInserted(prev, chatList.size)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivMedia: ImageView
        var tvText: TextView
        var tvName: TextView
        var root:LinearLayout


        init {
            ivMedia = itemView.findViewById(R.id.ivMedia)
            tvText = itemView.findViewById(R.id.tvText)
            tvName = itemView.findViewById(R.id.tvName)
            root = itemView.findViewById(R.id.root)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chat = list[position]
        return chat.isReceiver
    }

    init {
        this.context = context
    }
}
