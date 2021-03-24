package fr.uge.lootin.chat.services

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import fr.uge.lootin.chat.adapter.ChatAdapter
import fr.uge.lootin.chat.models.MessagePicture
import fr.uge.lootin.chat.models.MessagePictureResponse
import io.reactivex.Completable
import io.reactivex.CompletableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.security.spec.PSSParameterSpec.DEFAULT
import java.util.*


class MessagePictureService(private val adapter: ChatAdapter, private val recyclerView: RecyclerView, private val context: Context, private val url: String, private val myId: Long) {
    private var mStompClient: StompClient? = null
    private val mGson = GsonBuilder().create()
    private var compositeDisposable: CompositeDisposable? = null
    private val headers: MutableList<StompHeader> = ArrayList()

    /**
     * Create stomp web socket
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createStomp(){
        mStompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                url
        )
        resetSubscriptions()
        connectStomp()
        connectTopic()
    }

    /**
     * Send web socket messages
     */
    fun sendMessage(byteArray: String) {
        val m : MessagePicture = MessagePicture(byteArray, myId)
        if (!mStompClient?.isConnected!!) return;
        compositeDisposable!!.add(
                mStompClient!!.send(
                        //TODO ou est-ce qu'on envoie
                        DEST_MESSAGE,
                        m.toJSON()
                )
                        .compose(applySchedulers())
                        .subscribe(
                                {
                                    Log.d(
                                            TAG,
                                            "STOMP text message send successfully"
                                    )
                                }
                        ) { throwable: Throwable ->
                            Log.e(TAG, "Error send STOMP echo", throwable)
                            toast(throwable.message)
                        })
    }

    /**
     * Connect stomp web socket to the server
     */
    private fun connectStomp() {
        //headers.add(StompHeader(ChatActivity.LOGIN, "guest"))
        //headers.add(StompHeader(ChatActivity.PASSCODE, "guest"))
        mStompClient!!.withClientHeartbeat(1000).withServerHeartbeat(1000)
        resetSubscriptions()
        val dispLifecycle = mStompClient!!.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> toast("Stomp connection opened")
                    LifecycleEvent.Type.ERROR -> {
                        Log.e(
                                TAG,
                                "Stomp connection error",
                                lifecycleEvent.exception
                        )
                        toast("Stomp connection error")
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        toast("Stomp connection closed")
                        resetSubscriptions()
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> toast("Stomp failed server heartbeat")
                    else -> toast("WTF Error connect stomp MessagePictureService")
                }
            }
        compositeDisposable!!.add(dispLifecycle)
    }

    /**
     * Connect to topic and receive messages
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun connectTopic(){
        val dispTopic = mStompClient!!.topic(TOPIC)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { topicMessage: StompMessage ->
                        Log.d(
                                TAG,
                                "Received " + topicMessage.payload
                        )
                        Log.d(TAG, "on push dans connectTopic")

                        addItem(mGson.fromJson(topicMessage.payload, MessagePictureResponse::class.java))
                    }
            ) { throwable: Throwable? ->
                Log.e(
                        TAG,
                        "Error on subscribe topic",
                        throwable
                )
            }
        compositeDisposable!!.add(dispTopic)
        mStompClient!!.connect(headers)
    }

    /**
     * Add a picture message to recyclerview
     * TODO changer le model pour qu'il accepte des bitmap
     */
    private fun addItem(message: MessagePictureResponse) {
       /* adapter.pushMessage(MessageItemUi.factoryPictureItemUI(
            bitmap,
            message.id,
            message.date,
            myId == message.id_author
        ))
        recyclerView.scrollToPosition(adapter.itemCount - 1)
        LocalDate
        */
        Log.d(TAG, "on push un element")
    }


    private fun applySchedulers(): CompletableTransformer {
        return CompletableTransformer { upstream: Completable ->
            upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
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
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "--ACTIVITY--PICTURE"
        private const val TOPIC = "TODO"
        private const val DEST_MESSAGE = "TODO"
    }
}