package com.sribs.bdd.rx

class ButtonClickEvent(var id: String, var checked: Boolean) {

    var eventId: String = ""
    var checkedStatus: Boolean = false
    init{
        this.eventId = id
        this.checkedStatus = checked
    }
}