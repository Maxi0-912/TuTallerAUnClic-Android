package com.manuel.tutalleraunclic.data.model.request

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    val username: String? = null,
    @SerializedName("first_name") val first_name: String? = null,
    @SerializedName("last_name") val last_name: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val is_active: Boolean? = null
)
