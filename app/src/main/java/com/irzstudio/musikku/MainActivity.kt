package com.irzstudio.musikku

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InterruptedIOException

class MainActivity : AppCompatActivity() {

    private val mp: MediaPlayer by lazy {
        MediaPlayer.create(this, R.raw.music)
    }

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private var totalTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mp.setVolume(0.5f, 0.5f)
        totalTime = mp.duration

        playBtnClick()
        progressBar()
        setRunnable()
        setHandler()
        setMusicFinish()

    }

    private fun progressBar() {
        position_bar.max = totalTime
        position_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mp.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun playBtnClick() {
        btn_play.setOnClickListener {
            if (mp.isPlaying) {
                mp.pause()
                btn_play.setImageResource(R.drawable.ic_play)
            } else {
                mp.start()
                btn_play.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    private fun setRunnable() {
        Thread(Runnable {
            while (mp != null) {
                try {
                    val msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedIOException) {
                }
            }
        }).start()
    }

    private fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    private fun setHandler() {

        handler = object : Handler() {
            @SuppressLint("HandlerLeak")
            override fun handleMessage(msg: Message) {
                val currentPosition = msg.what

                position_bar.progress = currentPosition

                val elapsedTime = createTimeLabel(currentPosition)
                elapsed_timeLabel.text = elapsedTime

                val remainingTime = createTimeLabel(totalTime - currentPosition)
                remaining_timeLabel.text = "-$remainingTime"

            }
        }
    }

    private fun setMusicFinish() {
        runnable = Runnable {
            position_bar.progress = mp.currentPosition
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
        mp.setOnCompletionListener {
            btn_play.setImageResource(R.drawable.ic_play)
            position_bar.progress = 0
        }
    }

}