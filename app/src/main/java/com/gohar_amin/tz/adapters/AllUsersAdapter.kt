package com.gohar_amin.tz.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.acitivities.ui.ChatActivity
import com.gohar_amin.tz.model.User
import com.gohar_amin.tz.utils.JsonParser
import com.gohar_amin.tz.utils.Utils


class AllUsersAdapter(
    list: List<User>,
    context: Context,
    private val navController:NavController
) :
    RecyclerView.Adapter<AllUsersAdapter.MyViewHolder>() {
    var list: List<User>
    var searchedList: List<User>
    var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate: View = LayoutInflater.from(context)
            .inflate(R.layout.all_users_profile_item_layout, parent, false)
        return MyViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val profile= list[position]
        holder.name.setText(profile.email)
        Utils.loadImage(context, profile.imageUrl, holder.profile_image)
        /*holder.ivDelete.setOnClickListener { v ->
            if (!list.isEmpty()) {
                callback.onDelete(profile)
                list.remove(profile)
            }
        }
        holder.ivEdit.setOnClickListener { v -> callback.onEdit(profile) }*/
        holder.llRoot.setOnClickListener { v: View? ->
            context.startActivity(Intent(context,ChatActivity::class.java)
                .putExtra("openChat",profile.id)
                .putExtra("recevierUser",JsonParser.toJson(profile))
                .putExtra("allUsers",JsonParser.toJson(list))
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile_image: ImageView
        var name: TextView
        var llRoot: LinearLayout


        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            name = itemView.findViewById(R.id.name)
            llRoot = itemView.findViewById(R.id.llRoot)
        }
    }

    companion object {
        const val RECEIVER_ID = "receiverId"
    }

    init {
        this.list = list
        searchedList = ArrayList(list)
        this.context = context
    }
}
