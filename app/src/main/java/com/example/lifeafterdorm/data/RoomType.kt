package com.example.lifeafterdorm.data

data class RoomType(var singleRoom:Boolean = false,
                    var middleRoom:Boolean = false,
                    var masterRoom:Boolean = false,
                    var studio:Boolean = false,
                    var soho:Boolean = false,
                    var suite:Boolean = false,
                    var privateRoom:Boolean = false,
                    var sharedRoom:Boolean = false)
