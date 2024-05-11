package com.example.lifeafterdorm.data

data class UserPreferences(
    var id:String = "",
    var maxBudget:Double = 0.0,
    var minBudget:Double = 0.0,
    var singleRoom:Boolean = false,
    var middleRoom:Boolean = false,
    var masterRoom:Boolean = false,
    var studio:Boolean = false,
    var soho:Boolean = false,
    var suite:Boolean = false,
    var privateRoom:Boolean = false,
    var sharedRoom:Boolean = false
)
