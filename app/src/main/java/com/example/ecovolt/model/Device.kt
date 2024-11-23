package com.example.ecovolt.model

data class Device(
    var name: String,
    var consumption: Int,
    var type: String,
    var percent : Int? = null,
    var picPath: String? = null
) {
    constructor() : this("", 0, "")

    init {
        println(consumption)

        percent = (consumption * 100) / 150

        picPath = when (type.trim().lowercase()) {
            "eletrodoméstico cozinha" -> "kitchen_36"
            "eletrodoméstico lavanderia" -> "laundry_36"
            "climatização" -> "air_36"
            "iluminação" -> "lightbulb_36"
            "entretenimento e eletrônicos" -> "tv_36"
            "eletroportáteis" -> "iron_36"
            else -> "dashboard_36"
        }
    }
}
