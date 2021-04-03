package fr.uge.lootin.chat.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class DateConvertor {

    companion object {

        val TAG = "DAAAATE"
        fun convert(dateString: String) : String {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var outFormat : DateTimeFormatter =  DateTimeFormatter.ofPattern("HH:mm")
                try {
                    val date : LocalDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME) ?: return dateString
                    val days: Long = ChronoUnit.DAYS.between(date, LocalDateTime.now())
                    val hours: Long = ChronoUnit.HOURS.between(date, LocalDateTime.now())


                    if(days >= 7){
                        outFormat = DateTimeFormatter.ofPattern("dd LLLL");
                    }

                    else if(days >= 1){
                        outFormat = DateTimeFormatter.ofPattern("E, HH:mm");
                    }


                    return date.format(outFormat);

                } catch (e: Exception) {
                    Log.d(TAG,"error while parsing date" )
                    return dateString
                }
            }

            return dateString
        }
    }

}