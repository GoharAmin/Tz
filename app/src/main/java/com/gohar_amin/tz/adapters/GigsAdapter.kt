package com.gohar_amin.tz.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gohar_amin.tz.R
import com.gohar_amin.tz.acitivities.GigDetailActivity
import com.gohar_amin.tz.model.Category
import com.gohar_amin.tz.model.Gig
import com.gohar_amin.tz.model.UserGig
import com.gohar_amin.tz.utils.PrefHelper
import com.gohar_amin.tz.utils.Utils
import com.google.gson.Gson

class GigsAdapter(private val context: Context, private  val list:List<UserGig>, private val isGrid: Boolean) : RecyclerView.Adapter<GigsAdapter.MyViewHolder>() {
    private lateinit var prefHelper: PrefHelper
    private var id:String?=null
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var imageView: ImageView
        lateinit var tvName: TextView
        lateinit var tvRaters: TextView
        lateinit var tvPrice: TextView
        lateinit var tvRating: TextView
        lateinit var tvDescrittion: TextView
        lateinit var ratingbar:RatingBar
        lateinit var root: CardView
        init {
            tvName=itemView.findViewById(R.id.tvName)
            imageView=itemView.findViewById(R.id.imageView)
            tvRaters=itemView.findViewById(R.id.tvItemRaters)
            tvPrice=itemView.findViewById(R.id.tvItemPrice)
            tvRating=itemView.findViewById(R.id.tvRating)
            root=itemView.findViewById(R.id.root)
            tvDescrittion=itemView.findViewById(R.id.tvDescrittion)
            ratingbar=itemView.findViewById(R.id.ratingbar)
        }
    }
init {
    prefHelper= PrefHelper.getInstance(context)!!
    id=prefHelper.getString("userId");
}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.gigs_items_layout,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val gig=list.get(position)
        holder.tvName.setText(gig.title)
        holder.tvPrice.setText("$ "+gig.cost)
        holder.tvDescrittion.setText(""+gig.detail)
        Utils.loadImage(context,gig.ImageUrl,holder.imageView)
        //holder.tvRaters.setText(""+gig.r)
        //holder.tvRating.setText(""+gig.rating)
        val params = holder.root.layoutParams
        if(isGrid){
            params.width=LinearLayout.LayoutParams.MATCH_PARENT
        }else{
            /*if(id.equals(gig.uid)){
                holder.root.visibility=View.GONE
            }else{
                holder.root.visibility=View.VISIBLE
            }*/
            params.width=LinearLayout.LayoutParams.WRAP_CONTENT
        }
        holder.root.setOnClickListener {
            val i=Intent(context,GigDetailActivity::class.java)
            i.putExtra("gigDetail",Gson().toJson(gig))
            context.startActivity(i)
        }
        holder.root.layoutParams = params
            holder.tvRating.setText(""+gig.rating)
            holder.tvRaters.setText(" ("+gig.raters+") ")
            holder.ratingbar.rating=gig.rating!!
    }

    override fun getItemCount(): Int {
        return list.size
    }
}