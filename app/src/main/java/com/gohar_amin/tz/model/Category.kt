package com.gohar_amin.tz.model

class Category(var name:String){
    fun getInitials(): Char {
     return  name.toCharArray().get(0)
    }
}