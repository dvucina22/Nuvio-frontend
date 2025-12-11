package com.example.core.auth

enum class RoleType(val apiName: String) {
    ADMIN("admin"),
    SELLER("seller"),
    BUYER("buyer");

    companion object {
        fun fromApiName(name: String): RoleType? = entries.firstOrNull { it.apiName.equals(name, ignoreCase = true) }
    }
}
