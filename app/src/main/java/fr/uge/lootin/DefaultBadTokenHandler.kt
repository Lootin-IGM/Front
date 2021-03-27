package fr.uge.lootin

import android.content.Intent
import android.os.Build
import android.util.Log
import fr.uge.lootin.signin.SignInActivity
import kotlinx.coroutines.flow.SharingCommand

class DefaultBadTokenHandler {

    companion object{
        fun handleBadRequest (packageContext : android.content.Context){
           Intent(packageContext, NotificationsService::class.java).also {
               it.action = SharingCommand.STOP.name
               it.putExtra("userToken", "999")
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   packageContext.startForegroundService(it)
               }else{
                   packageContext.startService(it)
               }
           }
           val intent = Intent(packageContext, SignInActivity::class.java)
           packageContext.startActivity(intent)
       }
    }
}