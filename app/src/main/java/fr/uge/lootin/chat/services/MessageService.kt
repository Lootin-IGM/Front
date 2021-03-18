package fr.uge.lootin.chat.services

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.GsonBuilder
import fr.uge.lootin.chat.ChatActivity
import fr.uge.lootin.chat.adapter.ChatAdapter
import fr.uge.lootin.chat.models.MessageModel

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
import java.util.ArrayList


class MessageService(private val adapter: ChatAdapter, private val context: Context, private val url: String) {
    private var mStompClient: StompClient? = null
    private val mGson = GsonBuilder().create()
    private var compositeDisposable: CompositeDisposable? = null
    private val headers: MutableList<StompHeader> = ArrayList()

    fun createStomp(){
        mStompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            url
        )
        resetSubscriptions()
        connectStomp()
        connectTopic("/topic/greetings")
    }

    fun disconnect() {
        mStompClient!!.disconnect()
        if (compositeDisposable != null) compositeDisposable!!.dispose()
    }

    fun sendEchoViaStomp(v: View?) {
        val m : MessageModel = MessageModel("Thomas")
        if (!mStompClient?.isConnected!!) return;
        compositeDisposable!!.add(
            mStompClient!!.send(
                "/app/hello",
                m.toJSON()
            )
                .compose(applySchedulers())
                .subscribe(
                    {
                        Log.d(
                            TAG,
                            "STOMP echo send successfully"
                        )
                    }
                ) { throwable: Throwable ->
                    Log.e(TAG, "Error send STOMP echo", throwable)
                    toast(throwable.message)
                })
    }

    private fun connectStomp() {
        headers.add(StompHeader(ChatActivity.LOGIN, "guest"))
        headers.add(StompHeader(ChatActivity.PASSCODE, "guest"))
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

    private fun connectTopic(topic : String){
        val dispTopic = mStompClient!!.topic(topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { topicMessage: StompMessage ->
                    Log.d(
                        TAG,
                        "Received " + topicMessage.payload
                    )
                    Log.d(TAG, "on push dans connectstomp")

                    addItem(mGson.fromJson(topicMessage.payload, MessageModel::class.java))
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

    private fun addItem(echoModel: MessageModel) {
        //TODO adapter.pushFrontFirst(echoModel)
        Log.d(TAG, "on push dans addItem")
    }

    private fun toast(text: String?) {
        Log.i(TAG, text!!)
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MessageSTOMP"
    }
}