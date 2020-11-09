package com.gohar_amin.tz.model

class Product {
    var name:String?=null;
    var time:String?=null;
    var type:String?=null;
    var rating:Float=0.0f;
    var qty:Int=0;
    var price:Int=0;
    var imageUrl:String?=null;
    fun getInitials(): String {
        if(name!=null) {
            return ""+name!!.toCharArray().get(0)
        }else{
            return "null"
        }
    }
}