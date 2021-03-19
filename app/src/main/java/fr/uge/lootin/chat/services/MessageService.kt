package fr.uge.lootin.chat.services

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import fr.uge.lootin.chat.adapter.ChatAdapter
import fr.uge.lootin.chat.adapter.MessageItemUi
import fr.uge.lootin.chat.models.MessageText
import fr.uge.lootin.chat.models.MessageTextResponse

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
import java.time.Instant
import java.util.*


class MessageService(private val adapter: ChatAdapter, private val recyclerView: RecyclerView, private val context: Context, private val url: String, private val myId: Long) {
    private var mStompClient: StompClient? = null
    private val mGson = GsonBuilder().create()
    private var compositeDisposable: CompositeDisposable? = null
    private val headers: MutableList<StompHeader> = ArrayList()

    /**
     * Create stomp web socket
     */
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(v: View?, ) {
        val m : MessageText = MessageText("Hello WebSocket World", myId, Date.from(Instant.now()))
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
                }
            }
        compositeDisposable!!.add(dispLifecycle)
    }

    /**
     * Connect to topic and receive messages
     */
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
                    Log.d(TAG, "on push dans connectstomp")

                    addItem(mGson.fromJson(topicMessage.payload, MessageTextResponse::class.java))
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

    private fun addItem(message: MessageTextResponse) {
        adapter.pushOldMessage(MessageItemUi.factoryMessageItemUI(message.content, message.id, message.date, myId == message.id_author))
        recyclerView.scrollToPosition(adapter.itemCount - 1)
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
        private const val TAG = "MessageSTOMP"
        private const val TOPIC = "/topic/greetings"
        private const val DEST_MESSAGE = "/app/hello"
    }
}