package com.islate.silentpass.permissions

import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.hasCallScreeningRole(roleManager: RoleManager?): Boolean =
    roleManager?.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) == true &&
        roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
