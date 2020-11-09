package com.gohar_amin.tz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.model.Product

class CheckoutAdapter(private val context: Context, private val list:List<Product>): RecyclerView.Adapter<CheckoutAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var tvPrice:TextView
        lateinit var tvQty:TextView
        lateinit var tvName:TextView
        init {
            tvName=itemView.findViewById(R.id.tvPrice)
            tvQty=itemView.findViewById(R.id.tvQty)
            tvPrice=itemView.findViewById(R.id.tvName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.checkout_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product = list.get(position)
        holder.tvName.setText(""+product.name);
        holder.tvPrice.setText("$"+product.price);
        holder.tvQty.setText(""+product.qty);
    }

    override fun getItemCount(): Int {
        return list.size
    }
}