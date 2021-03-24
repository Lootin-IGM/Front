package fr.uge.lootin.chat.services

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import fr.uge.lootin.chat.ImageUtil
import fr.uge.lootin.chat.adapter.ChatAdapter
import fr.uge.lootin.chat.adapter.MessageItemUi
import fr.uge.lootin.chat.models.MessagePicture
import fr.uge.lootin.chat.models.MessageText
import fr.uge.lootin.chat.models.MessagesResponse
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
import java.util.*


class MessageTextService(private val adapter: ChatAdapter, private val recyclerView: RecyclerView, private val context: Context, private val url: String, private val myId: Long) {
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
        connectTopicPicture()
        mStompClient!!.connect(headers)
    }

    /**
     * Send web socket messages
     */
    fun sendMessage(message: String) {
        val m : MessageText = MessageText(message, myId)
        if (!mStompClient?.isConnected!!) return;
        compositeDisposable!!.add(
                mStompClient!!.send(
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
                                    Log.d(TAG, "data send : ${m.toJSON()}")
                                }
                        ) { throwable: Throwable ->
                            Log.e(TAG, "Error send STOMP echo", throwable)
                            toast(throwable.message)
                        })
    }

    /**
     * Send web socket messages
     */
    fun sendPicture(byteArray: String?) {
        val m : MessagePicture? = byteArray?.let { MessagePicture(it, myId) }
        if (!mStompClient?.isConnected!!) return;
        if (m != null) {
            Log.d(TAG, "picture send => " + m.toJSON())
        }
        if (m != null) {
            compositeDisposable!!.add(
                    mStompClient!!.send(
                            //TODO ou est-ce qu'on envoie
                            DEST_PICTURE,
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
    }

    /**
     * Connect stomp web socket to the server
     */
    private fun connectStomp() {
        //headers.add(StompHeader(ChatActivity.LOGIN, "guest"))
        headers.add(StompHeader("X-Authorization", "Bearer $myId"))
        mStompClient!!.withClientHeartbeat(1000).withServerHeartbeat(1000)
        resetSubscriptions()
        Log.d(TAG, "try connect stomp")
        val dispLifecycle = mStompClient!!.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> Log.d(TAG, "Stomp connection opened ${lifecycleEvent.handshakeResponseHeaders}")
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
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.d(TAG, "Stomp failed server heartbeat")
                    else -> Log.d(TAG, "WTF Error connect stomp MessageTextService")
                }
            }
        compositeDisposable!!.add(dispLifecycle)

    }

    /**
     * Connect to topic and receive messages
     */
    private fun connectTopic(){
        val dispTopic = mStompClient!!.topic("/user/$myId/text")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { topicMessage: StompMessage ->
                        Log.d(
                                TAG,
                                "Received " + topicMessage.payload
                        )
                        Log.d(TAG, "on push dans connectstomp")

                        addItem(mGson.fromJson(topicMessage.payload, MessagesResponse.Message::class.java))
                    }
            ) { throwable: Throwable? ->
                Log.e(
                        TAG,
                        "Error on subscribe topic",
                        throwable
                )
            }
        Log.d(TAG, "subscribe in channel : /user/$myId/text")
        compositeDisposable!!.add(dispTopic)

    }

    /**
     * Connect to topic and receive messages
     */
    private fun connectTopicPicture(){
        val dispTopic = mStompClient!!.topic("/user/$myId/picture")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { topicMessage: StompMessage ->
                            Log.d(
                                    TAG,
                                    "Received picture " + topicMessage.payload
                            )
                            Log.d(TAG, "on push dans connectstomp")


                            addItemPicture(mGson.fromJson(topicMessage.payload, MessagesResponse.Picture::class.java))
                        }
                ) { throwable: Throwable? ->
                    Log.e(
                            TAG,
                            "Error on subscribe topic",
                            throwable
                    )
                }
        Log.d(TAG, "subscribe in channel : /user/$myId/picture")
        compositeDisposable!!.add(dispTopic)
    }

    /**
     * Add a text message to recyclerview
     */
    private fun addItem(message: MessagesResponse.Message) {
        adapter.pushMessage(MessageItemUi.factoryMessageItemUI(
                message.message,
                1L,
                message.sendTime,
                myId == message.sender
        ))
        recyclerView.scrollToPosition(adapter.itemCount - 1)
        Log.d(TAG, "on push un element")
    }

    /**
     * Add a text message to recyclerview
     */
    private fun addItemPicture(message: MessagesResponse.Picture) {

        val bitmap: Bitmap = ImageUtil.convert(message.picture)
        adapter.pushMessage(MessageItemUi.factoryPictureItemUI(
                bitmap,
                message.id,
                message.sendTime,
                myId == message.sender
        ))
         recyclerView.scrollToPosition(adapter.itemCount - 1)

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
        private const val TAG = "--ACTIVITY--TEXT"
        //private const val TOPIC = "/topic/greetings"
        private const val TOPIC = "/user/queue/specific-user-user"

        private const val DEST_MESSAGE = "/spring-security-mvc-socket/bonjour"
        private const val DEST_PICTURE = "/spring-security-mvc-socket/picture"
    }
}