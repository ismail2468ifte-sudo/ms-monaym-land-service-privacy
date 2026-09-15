package com.msmonaym.land.data

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import java.io.File

data class PermissionStatus(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val isGranted: Boolean,
    val permissionKey: String,
    val requiredFor: String
)

object PermissionHelper {

    fun isCameraGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationGranted(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    fun isNotificationGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isCallPhoneGranted(context: Context): Boolean {
        // App uses Intent.ACTION_DIAL which does not require dangerous CALL_PHONE permission
        return true
    }

    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun getAllPermissionStatuses(context: Context): List<PermissionStatus> {
        val isCam = isCameraGranted(context)
        val isLoc = isLocationGranted(context)
        val isNotif = isNotificationGranted(context)
        val isNet = isNetworkConnected(context)

        return listOf(
            PermissionStatus(
                id = "camera",
                title = "ক্যামেরা পারমিশন (Camera)",
                subtitle = if (isCam) "সক্রিয় আছে (অনুমোদিত)" else "অনুমতি দেওয়া প্রয়োজন",
                iconEmoji = "📷",
                isGranted = isCam,
                permissionKey = Manifest.permission.CAMERA,
                requiredFor = "দলিল, খতিয়ান ও সার্ভে পেপারের ছবি স্ক্যান করতে"
            ),
            PermissionStatus(
                id = "location",
                title = "জিপিএস ও ম্যাপ লোকেশন (GPS Location)",
                subtitle = if (isLoc) "সক্রিয় আছে (অনুমোদিত)" else "অনুমতি দেওয়া প্রয়োজন",
                iconEmoji = "🛰️",
                isGranted = isLoc,
                permissionKey = Manifest.permission.ACCESS_FINE_LOCATION,
                requiredFor = "স্যাটেলাইট ম্যাপ ও লাইভ জমি পরিমাপ করতে"
            ),
            PermissionStatus(
                id = "storage",
                title = "ফাইল ও স্টোরেজ এক্সেস (Files & PDF)",
                subtitle = "সক্রিয় আছে (Scoped Storage / A4 PDF Export)",
                iconEmoji = "📁",
                isGranted = true,
                permissionKey = "STORAGE_SCOPED",
                requiredFor = "PDF সার্ভে রিপোর্ট ও রসিদ ডাউনলোড ও শেয়ার করতে"
            ),
            PermissionStatus(
                id = "notification",
                title = "নোটিফিকেশন পারমিশন (Notifications)",
                subtitle = if (isNotif) "সক্রিয় আছে (অনুমোদিত)" else "অনুমতি দেওয়া প্রয়োজন",
                iconEmoji = "🔔",
                isGranted = isNotif,
                permissionKey = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else "",
                requiredFor = "ভূমি সংক্রান্ত নোটিশ, খতিয়ান ও রসিদ অ্যালার্ট পেতে"
            ),
            PermissionStatus(
                id = "phone",
                title = "হটলাইন ডায়াল ও কল (Phone Dialer)",
                subtitle = "সরাসরি নিরাপদ ডায়ালার সক্রিয় (Play Store Compliant)",
                iconEmoji = "📞",
                isGranted = true,
                permissionKey = "",
                requiredFor = "১৬১২২ ভূমি হটলাইন ও কাস্টমার সাপোর্টে কল ডায়াল করতে"
            ),
            PermissionStatus(
                id = "internet",
                title = "ইন্টারনেট ও নেটওয়ার্ক (Internet / API)",
                subtitle = if (isNet) "অনলাইন সংযুক্ত (Online)" else "অফলাইন মোড",
                iconEmoji = "🌐",
                isGranted = isNet,
                permissionKey = Manifest.permission.INTERNET,
                requiredFor = "Gemini 2.5 এআই ও সরকারি ভূমি ডাটাবেজ এক্সেস"
            )
        )
    }

    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val genericSettings = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(genericSettings)
        }
    }

    fun getAppCacheSize(context: Context): String {
        var size: Long = 0
        try {
            size += getFolderSize(context.cacheDir)
            context.externalCacheDir?.let { size += getFolderSize(it) }
        } catch (_: Exception) {}
        return formatFileSize(size)
    }

    fun clearAppCache(context: Context): Boolean {
        return try {
            deleteDir(context.cacheDir)
            context.externalCacheDir?.let { deleteDir(it) }
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        if (file.isDirectory) {
            file.listFiles()?.forEach { subFile ->
                size += if (subFile.isDirectory) getFolderSize(subFile) else subFile.length()
            }
        } else {
            size = file.length()
        }
        return size
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list() ?: return false
            for (child in children) {
                val success = deleteDir(File(dir, child))
                if (!success) return false
            }
            return dir.delete()
        } else if (dir != null && dir.isFile) {
            return dir.delete()
        }
        return false
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 KB"
        val kb = size / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1.0) {
            String.format("%.2f MB", mb)
        } else {
            String.format("%.1f KB", kb)
        }
    }
}
