package com.wehack.cinlocation

class Item {

    var background: Int = 0
    internal var title: String = ""
    internal var location: String = ""

    constructor() {}

    constructor(background: Int, title: String, location: String) {
        this.background = background
        this.title = title
        this.location = location
    }

}