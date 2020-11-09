package com.gohar_amin.tz.model

import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson

class UserGig {
    var id:String?=null
    var uid:String?=null
    var title:String?=null
    var deliverTime:String?=null
    var category:String="none"
    var fastDeliverProduct:String?=null
    var fastDeliverPrice:String="0"
    var fastDeliverDays:Int=1
    var workOptions:String="Remote"
    var detail:String?=null
    var ImageUrl:String?=null
    var requiredInfo:String?=null
    var cost:Int=0
    var purchased:Int=0
    var createdAt= ""+System.currentTimeMillis()
}