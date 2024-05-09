package com.example.lifeafterdorm.data

data class User(var id:String="",
                var name:String = "",
                var email:String = "" ,
                var gender:String = "",
                var password:String = "",
    var location:Location = Location(0.0, 0.0),
    var nationality:String = "",
    var phoneNum:String = "",
    var profileImg:String = ""
    )


