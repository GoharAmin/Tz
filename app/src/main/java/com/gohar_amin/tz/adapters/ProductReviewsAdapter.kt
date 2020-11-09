package com.gohar_amin.tz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.model.Product
import de.hdodenhof.circleimageview.CircleImageView

class ProductReviewsAdapter(private val context: Context, private val list:List<Product>): RecyclerView.Adapter<ProductReviewsAdapter.MyViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        return MyViewholder(LayoutInflater.from(context).inflate(R.layout.product_rating_item_layout,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val productRating=list.get(position)
        holder.ratingbar.rating=productRating.rating
        holder.tvName.setText(""+productRating.name+"("+productRating.time+")")
        holder.tvType.setText(""+productRating.type)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var tvInitials:TextView
        lateinit var imageView:CircleImageView
        lateinit var tvName:TextView
        lateinit var ratingbar:RatingBar
        lateinit var tvType:TextView
        init {
            tvInitials=itemView.findViewById(R.id.tvInitials)
            imageView=itemView.findViewById(R.id.imageView)
            tvName=itemView.findViewById(R.id.tvName)
            ratingbar=itemView.findViewById(R.id.ratingbar)
            tvType=itemView.findViewById(R.id.tvType)
        }
    }
}