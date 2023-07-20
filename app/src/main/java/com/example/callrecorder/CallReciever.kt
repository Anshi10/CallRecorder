package com.example.callrecorder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Environment
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.example.callrecorder.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException

class CallReciever : BroadcastReceiver() {

    private val mediaRecorder = MediaRecorder()
    private lateinit var filePath: String


    override fun onReceive(context: Context?, intent: Intent?) {

        defineFilePath()

        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            when (state) {

                TelephonyManager.EXTRA_STATE_OFFHOOK -> {//perform actions when an outgoing call is answered
                    //Handle call answered - store recording
                    callRecorder()
                    Toast.makeText(context, "Call Recording has started", Toast.LENGTH_LONG).show()
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {//perform actions when a call ends.
                    //Handle call ended - end recording
                    stopRecording()
                    Toast.makeText(context, "Call Recording has ended", Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "recording has saved to $filePath" , Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun defineFilePath() {
        val directory = File(Environment.getExternalStorageDirectory(), "Recordings")
        directory.mkdirs()

        val fileName = "recording${System.currentTimeMillis()}.mp4"
        filePath = File(directory, fileName).absolutePath
        Log.i("anshi", "defined file path")
    }

    private fun callRecorder() {
        Log.i("anshi", "Call Recording is going on")
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)


        mediaRecorder.setOutputFile(filePath)
        Log.i("anshi", "$filePath")

        try {
            // Prepare the MediaRecorder
            mediaRecorder.prepare()

            // Start the recording
            mediaRecorder.start()

        } catch (e: IOException) {
            // Handle any exceptions that occur during recording
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        Log.i("anshi", "call has ended")
        try {
            mediaRecorder.stop()
            mediaRecorder.reset()
            mediaRecorder.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
