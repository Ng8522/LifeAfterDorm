package com.example.lifeafterdorm.data

data class UserPreferences(
    var id:String = "",
    var maxBudget:Double = 0.0,
    var minBudget:Double = 0.0,
    var roomType: RoomType = RoomType(
        singleRoom = false,
        middleRoom = false,
        masterRoom = false,
        studio = false,
        soho = false,
        suite = false,
        privateRoom = false,
        sharedRoom = false
    )

)
