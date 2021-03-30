package fr.uge.lootin.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import fr.uge.lootin.DefaultBadTokenHandler
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
    private var contextActivity: Context? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.activity_chat, container, false)

        val port = Configuration.getPort(activity?.applicationContext!!)
        val ip = Configuration.getIp(activity?.applicationContext!!)

        // GET INFO from other activity
        val nameOther = requireArguments().getString(OTHER_NAME).toString()
        matchId = requireArguments().getLong(MATCH_ID, -1)
        idUser = requireArguments().getLong(USER_ID, -1)
        val byteArray = requireArguments().getByteArray(PICTURE_ID)


        val prefs = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
        val token = prefs.getString("jwt", "").toString()
        val idUserString = (prefs.getString("id", "").toString())
        val notifToken = prefs.getString("token", "").toString()

        if(token.isEmpty() || idUserString.isEmpty() || notifToken.isEmpty()){
            Log.d(TAG," ===============ONN INIIIIIITAILISE --------------")
            DefaultBadTokenHandler.handleBadRequest(contextActivity!!)
        }

        idUser = idUserString.toLong()
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
            nameOther,
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
            //Toast.makeText(contextActivity, "Send my picture", Toast.LENGTH_LONG).show()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
        }

        // Send picture messages (WS)
        layout.findViewById<ImageButton>(R.id.imageButtonPicture).setOnClickListener {
            Toast.makeText(contextActivity, "Send camera pictures", Toast.LENGTH_LONG).show()

            /*val intent =  Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

             */
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(intent, REQUEST_CODE_GALLERY)
        }
        layout.findViewById<TextView>(R.id.nameUser).text = nameOther

        layout.findViewById<ImageView>(R.id.retour).setOnClickListener {
            messageService.disconnect()
            activity?.supportFragmentManager?.popBackStack()
        }


        if (byteArray != null) {
            layout.findViewById<CircleImageView>(R.id.header_CIW).setImageBitmap( BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
        }



        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextActivity = context
    }


    /**
     * retour en fonction de la galerie ou de la capture
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var res: Bitmap? = null
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CAMERA && data != null) {
            Log.d(TAG, "--------200-------------")
             res  = data.extras!!.get("data") as Bitmap
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_GALLERY  && data != null) {
            Log.d(TAG, "--------100-------------")

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
            match_id: Long,
            //byteArray: ByteArray,
            othername: String
        ): ChatFragment {
            var fragment = ChatFragment()
            val args = Bundle()
            args.putLong(MATCH_ID, match_id)
            //args.putByteArray(PICTURE_ID, byteArray)
            args.putString(OTHER_NAME, othername)
            fragment.arguments = args
            return fragment
        }

        const val TAG = "--ACTIVITY--MAIN"

        const val TOKEN_VALUE = "fr.uge.lootin.TOKEN"
        const val MATCH_ID = "fr.uge.lootin.MATCHID"
        const val USER_ID = "fr.uge.lootin.USER_ID"
        const val PICTURE_ID = "fr.uge.lootin.USER_NAME"
        const val OTHER_NAME = "fr.uge.lootin.OTHER_NAME"

        const val REQUEST_CODE_GALLERY = 100
        const val REQUEST_CODE_CAMERA = 200

        const val PAGE_SIZE: Long = 10

        const val ENPOINT: String = "secured/room"
    }
}
