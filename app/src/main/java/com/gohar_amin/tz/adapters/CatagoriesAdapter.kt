package com.gohar_amin.tz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.model.Category

class CatagoriesAdapter(private val context:Context,private  val list:ArrayList<Category>) : RecyclerView.Adapter<CatagoriesAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         lateinit var tvInitials:TextView
        lateinit var tvName:TextView
        init {
            tvInitials=itemView.findViewById(R.id.initials)
            tvName=itemView.findViewById(R.id.tvName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.catagories_item_layout,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val category = list.get(position)
        holder.tvInitials.setText(""+category.getInitials())
        holder.tvName.setText(category.name)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}