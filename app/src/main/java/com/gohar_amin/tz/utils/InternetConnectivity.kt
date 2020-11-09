package aybrowser.ultra.taxibookingonline.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
object InternetConnectivity {
    fun isConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            ))
        } else {
            val info = manager.activeNetworkInfo
            info != null && info.isConnected && info.isAvailable
        }
    }
}