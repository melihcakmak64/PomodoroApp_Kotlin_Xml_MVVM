package com.example.pomodoroapp.view

import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.pomodoroapp.R
import com.example.pomodoroapp.databinding.ActivityMainBinding
import com.example.pomodoroapp.databinding.DialogOptionsBinding
import com.example.pomodoroapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var alertDialog: AlertDialog
    private var dialogOptionsBinding: DialogOptionsBinding? = null
    private val viewModel: MainViewModel by viewModels()
    private lateinit var countdownMediaPlayer: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        countdownMediaPlayer = MediaPlayer.create(this, R.raw.timer)


        binding.optionButton.setOnClickListener {
            showSettingsDialog()
        }

        binding.playButton.setOnClickListener {
            viewModel.togglePlayPause()
        }

        binding.nextButton.setOnClickListener {
            viewModel.moveToNextMode()
            countdownMediaPlayer.stop()
        }

        viewModel.isPlaying.observe(this, Observer { isPlaying ->
            if (isPlaying) {
                binding.playButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause))
            } else {
                binding.playButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_arrow))
            }
        })

        viewModel.time.observe(this, Observer { time ->
            val minutes = time / 60
            val seconds = time % 60
            val timeText = String.format("%02d\n%02d", minutes, seconds)
            binding.timeTxt.text = timeText

            if (time == 3) {
                countdownMediaPlayer.start()
            }
        })

        viewModel.mode.observe(this, Observer { mode ->
            binding.backgroundMain.setBackgroundColor(resources.getColor(mode.backgroundColor))
            updateButtonBackgroundColor(binding.playButton, mode.playColor)
            updateButtonBackgroundColor(binding.optionButton, mode.buttonColor)
            updateButtonBackgroundColor(binding.nextButton, mode.buttonColor)
            binding.pomoMode.setBackgroundColor(resources.getColor(mode.buttonColor))
            binding.timeTxt.setTextColor(resources.getColor(mode.clockColor))
            binding.stateText.text=mode.modeName
        })
    }

    private fun updateButtonBackgroundColor(button: View, colorResId: Int) {
        val background = button.background as GradientDrawable
        background.setColor(ContextCompat.getColor(this, colorResId))
    }

    private fun showSettingsDialog() {
        if (dialogOptionsBinding == null) {
            dialogOptionsBinding = DialogOptionsBinding.inflate(layoutInflater).apply {
                pomoLenghtDecButton.setOnClickListener {
                    viewModel.updateModeDuration(MainViewModel.Mode.POMO, -1)
                    pomoLenghtTxt.text = MainViewModel.Mode.POMO.duration.toString()
                }
                pomoLenghtIncButton.setOnClickListener {
                    viewModel.updateModeDuration(MainViewModel.Mode.POMO, 1)
                    pomoLenghtTxt.text = MainViewModel.Mode.POMO.duration.toString()
                }
                shortBreakDecLnghtButton.setOnClickListener {
                    viewModel.updateModeDuration(MainViewModel.Mode.SHORT_BREAK, -1)
                    shortLenghtTxt.text = MainViewModel.Mode.SHORT_BREAK.duration.toString()
                }
                shortBreakIncLnghtButton.setOnClickListener {
                    viewModel.updateModeDuration(MainViewModel.Mode.SHORT_BREAK, 1)
                    shortLenghtTxt.text = MainViewModel.Mode.SHORT_BREAK.duration.toString()
                }
                longBreakDecLnghtButton.setOnClickListener {
                    viewModel.updateModeDuration(MainViewModel.Mode.LONG_BREAK, -1)
                    longLenghtTxt.text = MainViewModel.Mode.LONG_BREAK.duration.toString()
                }
                longBreakIncLnghtButton.setOnClickListener {
                    viewModel.updateModeDuration(MainViewModel.Mode.LONG_BREAK, 1)
                    longLenghtTxt.text = MainViewModel.Mode.LONG_BREAK.duration.toString()
                }
            }





        val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogOptionsBinding!!.root)
            alertDialog = dialogBuilder.create()
        }
        dialogOptionsBinding?.apply {
            pomoLenghtTxt.text = MainViewModel.Mode.POMO.duration.toString()
            shortLenghtTxt.text = MainViewModel.Mode.SHORT_BREAK.duration.toString()
            longLenghtTxt.text = MainViewModel.Mode.LONG_BREAK.duration.toString()
        }


        alertDialog.show()
    }

    fun closeDialog(view: View) {
        alertDialog.dismiss()
    }
}
