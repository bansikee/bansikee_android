package com.tomasandfriends.bansikee.src

import com.google.gson.annotations.SerializedName

open class DefaultResponse {
    @SerializedName("data")
    var data = ""
        protected set
    @SerializedName("detail")
    var detail = ""
        protected set
    @SerializedName("status")
    var status = 0
        protected set
    @SerializedName("title")
    var title = ""
        protected set
}