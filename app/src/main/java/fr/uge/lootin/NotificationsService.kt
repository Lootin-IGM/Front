package fr.uge.lootin

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingCommand
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.*


class NotificationsService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false
    private var mStompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null
    private val headers: MutableList<StompHeader> = ArrayList()
    private lateinit var instance: NotificationsService
    private var notifyNumber = 0
    private val NOTIFICATION_CHANNEL_ID = "com.example.simpleapp"
    private val channelName = "My Background Service"
    private lateinit var userToken: String

    override fun onBind(intent: Intent): IBinder? {
        Log.i("test", "Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("test", "onStartCommand executed with startId: $startId")
        if (intent != null) {
            userToken = intent.getStringExtra("userToken").toString()
            val action = intent.action
            Log.i("test", "using an intent with action $action")
            when (action) {
                SharingCommand.START.name -> startService(intent.getStringExtra("url").toString())
                SharingCommand.STOP.name -> stopService()
                else -> Log.i("test", "This should never happen. No action in the received intent")
            }
        } else {
            Log.i(
                "test",
                "with a null intent. It has been probably restarted by the system."
            )
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground()
        } else startForeground(
            1,
            Notification()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.RED
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(R.drawable.ic_lootin_logo)
            .build()
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, ProfilesSwipingActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationBuilder.setContentIntent(contentIntent)
        startForeground(2, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("test", "The service has been destroyed".toUpperCase())
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
    }

    private fun startService(url: String) {
        if (isServiceStarted) {
            return
        }
        Log.i("test", "Starting the foreground service task")
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                acquire()
            }
        }
        startWebSocketListener(url)
    }

    private fun stopService() {
        Log.i("test", "Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            Log.i("test", "Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
    }

    private fun startWebSocketListener(url: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)


        mStompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            "ws://$url:8080/secured/room"
        )
        resetSubscriptions()
        connectStomp()
        connectTopic()
        mStompClient!!.connect(headers)
    }

    private fun connectStomp() {
        headers.add(StompHeader("X-Authorization", "Bearer " + userToken))
        mStompClient!!.withClientHeartbeat(1000).withServerHeartbeat(1000)
        resetSubscriptions()
        Log.d(TAG, "try connect stomp")
        val dispLifecycle = mStompClient!!.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> Log.d(
                        TAG,
                        "Stomp connection opened ${lifecycleEvent.handshakeResponseHeaders}"
                    )
                    LifecycleEvent.Type.ERROR -> {
                        Log.e(
                            TAG,
                            "Stomp connection error",
                            lifecycleEvent.exception
                        )
                        toast("Stomp connection error")
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Log.d(TAG, "Stomp connection closed")
                        toast("Stomp connection closed")
                        resetSubscriptions()
                        relaunchLoop()
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.d(
                        TAG,
                        "Stomp failed server heartbeat"
                    )
                    else -> Log.d(TAG, "Error connect stomp MessageTextService")
                }
            }
        compositeDisposable!!.add(dispLifecycle)

    }

    private fun relaunchLoop() {
        Log.i("okay", "bite")
    }

    private fun connectTopic() {
        val dispTopic = mStompClient!!.topic("/user/" + userToken + "/notification")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { topicMessage: StompMessage ->
                    Log.d(
                        TAG,
                        "Received " + topicMessage.payload
                    )
                    notificator(topicMessage.payload)
                    Log.d(TAG, "on push dans connectstomp")
                }
            ) { throwable: Throwable? ->
                Log.e(
                    TAG,
                    "Error on subscribe topic",
                    throwable
                )
            }
        compositeDisposable!!.add(dispTopic)

    }

    private fun resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = CompositeDisposable()
    }

    /**
     * Disconnect web socket to the sever
     */
    fun disconnect() {
        mStompClient!!.disconnect()
        if (compositeDisposable != null) compositeDisposable!!.dispose()
    }

    private fun toast(text: String?) {
        Log.i(TAG, text!!)
    }

    companion object {
        private const val TAG = "--ACTIVITY--TEXT"
    }

    private fun notificator(payload: String) {
        val builder = NotificationCompat.Builder(this@NotificationsService, NOTIFICATION_CHANNEL_ID)
        var contentTitle = ""
        var contentText = ""
        if (payload.contains("message")) {
            contentTitle = "New message"
            contentText = "You have a new message !"
        }
        if (payload.contains("loot")) {
            contentTitle = "New loot"
            contentText = "You have a new loot !"
        }
        builder.setContentTitle(contentTitle)
        builder.setContentText(contentText)
        builder.setSmallIcon(R.drawable.ic_lootin_logo)
        builder.setAutoCancel(true);
        val managerCompat = NotificationManagerCompat.from(this@NotificationsService)


        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, ProfilesSwipingActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(contentIntent)
        managerCompat.notify(notifyNumber, builder.build())

        notifyNumber++
    }


}
