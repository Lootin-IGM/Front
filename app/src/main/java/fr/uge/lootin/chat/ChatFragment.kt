package fr.uge.lootin.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R
import fr.uge.lootin.chat.adapter.ChatAdapter
import fr.uge.lootin.chat.services.MessageTextService
import fr.uge.lootin.chat.services.RestService
import fr.uge.lootin.chat.utils.ImageUtil
import fr.uge.lootin.config.Configuration
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.properties.Delegates


class ChatFragment :  Fragment() {

    lateinit var messageService : MessageTextService
    lateinit var recycler: RecyclerView
    lateinit var adapter: ChatAdapter
    var idUser by Delegates.notNull<Long>()
    var matchId by Delegates.notNull<Long>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.activity_chat, container, false)

        val port = Configuration.getPort(activity?.applicationContext!!)
        val ip = Configuration.getIp(activity?.applicationContext!!)

        // GET INFO from other activity
        val token = requireArguments().getString(TOKEN_VALUE).toString()
        val nameOther = requireArguments().getString(OTHER_NAME).toString()
        matchId = requireArguments().getLong(MATCH_ID, -1)
        idUser = requireArguments().getLong(USER_ID, -1)

        if (matchId == -1L || idUser == -1L){
            //TODO Stop
            Log.e(TAG, "Probleme avec données récupérées")
        }

        // Create recycler and adapter
        recycler= layout.findViewById(R.id.reclyclerChat)
        adapter = ChatAdapter(ArrayList(), PAGE_SIZE)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(
            activity?.applicationContext,
            RecyclerView.VERTICAL,
            true
        )

        //create restService
        val restService = RestService(
            ip,
            port,
            matchId,
            PAGE_SIZE,
            adapter,
            token,
            idUser,
            activity?.applicationContext!!
        )
        restService.verifyConnect()
        //create web sockets services
        messageService = MessageTextService(
            adapter,
            recycler,
            activity?.applicationContext!!,
            "ws://$ip:$port/$ENPOINT",
            idUser,
            matchId
        )

        // Create scrollListener on recyclerview
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i(TAG, newState.toString())
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    restService.getMessages()
                    recycler.scrollToPosition(adapter.getItemCount() - 1)
                }
            }
        })

        restService.getMessages()
        messageService.createStomp()

        // Send Text messages
        layout.findViewById<ImageButton>(R.id.imageButtonsendText).setOnClickListener {
            val message : String = layout.findViewById<TextView>(R.id.zoneText).text.toString()
            if (message.isNotEmpty()) {
                messageService.sendMessage(message.replace("\\", "\\\\"))
                layout.findViewById<TextView>(R.id.zoneText).text = ""
            }
        }

        // Send camera picture messages (WS)
        layout.findViewById<ImageButton>(R.id.imageButtoncamera).setOnClickListener {
            Toast.makeText(activity?.applicationContext!!, "Send my picture", Toast.LENGTH_LONG).show()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 200)
        }

        // Send picture messages (WS)
        layout.findViewById<ImageButton>(R.id.imageButtonPicture).setOnClickListener {
            Toast.makeText(
                activity?.applicationContext!!,
                "Send camera pictures",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }
        layout.findViewById<TextView>(R.id.nameUser).text = nameOther

        layout.findViewById<Button>(R.id.retour).setOnClickListener {
            //TODO ça marche peut etre
            activity?.supportFragmentManager?.popBackStack()

        }

        return layout
    }


    /**
     * retour en fonction de la galerie ou de la capture
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var res: Bitmap? = null
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null) {
             res  = data.extras!!.get("data") as Bitmap
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 100  && data != null) {
            val imageView = ImageView(activity?.applicationContext!!)
            val pickedImage: Uri? = data.getData()
            // Let's read picked image path using content resolver
            // Let's read picked image path using content resolver
            //set the selected image to ImageView
            //set the selected image to ImageView

            imageView.setImageURI(pickedImage)
            //val drawable : BitmapDrawable = mImageView.getDrawable() as BitmapDrawable;

            res = imageView.drawable.toBitmap()
            //val bmap = imageView.drawingCache


            //res = (data.data as BitmapDrawable).bitmap // to get bitmap from imageView
            //res  = data?.extras!!.get("data") as Bitmap

        }





        val stream = ByteArrayOutputStream()
        res?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val array = stream.toByteArray()

        val base64String: String? = res?.let { ImageUtil.convert(it) }

        //send picture with web socket
        if (array != null) {
            messageService.sendPicture(base64String)
            recycler.scrollToPosition(0)
        }
    }

    companion object {

        fun chatInstance(
            token: String,
            match_id: Long,
            user_id: Long,
            username: String,
            othername: String
        ): ChatFragment {
            var fragment = ChatFragment()
            val args = Bundle()
            args.putString(TOKEN_VALUE, token)
            args.putLong(MATCH_ID, match_id)
            args.putLong(USER_ID, user_id)
            args.putString(USER_NAME, username)
            args.putString(OTHER_NAME, othername)
            fragment.arguments = args
            return fragment
        }

        const val TAG = "--ACTIVITY--MAIN"

        const val TOKEN_VALUE = "fr.uge.lootin.TOKEN"
        const val MATCH_ID = "fr.uge.lootin.MATCHID"
        const val USER_ID = "fr.uge.lootin.USER_ID"
        const val USER_NAME = "fr.uge.lootin.USER_NAME"
        const val OTHER_NAME = "fr.uge.lootin.OTHER_NAME"

        const val PAGE_SIZE: Long = 10

        const val ENPOINT: String = "secured/room"
    }
}
