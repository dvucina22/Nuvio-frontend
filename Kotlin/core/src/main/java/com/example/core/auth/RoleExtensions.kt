package com.example.core.auth

import com.example.core.auth.dto.Role

fun Role.toRoleType(): RoleType? = RoleType.fromApiName(name)
