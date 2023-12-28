package com.example.projectr.data

data class UserData(
    val userId: String? = "",
    val name: String? = "",
    val number: String? = "",
    val imageURL: String? = ""
) {
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "number" to number,
        "imageURL" to imageURL
    )
}