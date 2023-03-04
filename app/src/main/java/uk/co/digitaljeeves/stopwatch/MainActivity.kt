package uk.co.digitaljeeves.stopwatch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uk.co.digitaljeeves.stopwatch.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent
    private var isStarted = false
    private var time = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnStart.setOnClickListener{
                startOrStop()
            }
            btnReset.setOnClickListener {
                reset()
            }
        }
        serviceIntent = Intent(applicationContext, StopwatchService::class.java)
        registerReceiver(updateTime, IntentFilter(StopwatchService.UPDATED_TIME))
    }
    private fun startOrStop(){
        if (isStarted)
            stop()
        else
            start()
    }
    private fun start(){
        serviceIntent.putExtra(StopwatchService.CURRENT_TIME, time)
        startService(serviceIntent)
        isStarted = true
        binding.apply {
            btnStart.text = getString(R.string.stop)
        }
    }
    private fun stop(){
        stopService(serviceIntent)
        isStarted = false
        binding.apply {
            btnStart.text = getString(R.string.start)
        }
     }
    private fun reset(){
        stop()
        time = 0.0
        binding.tvTime.text = getFormatedTime(time)
    }
    private val updateTime : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(StopwatchService.CURRENT_TIME, 0.0)
            binding.tvTime.text = getFormatedTime(time)
        }

    }
    private fun getFormatedTime(time: Double):String{
        val timeInt = time.roundToInt()
        val hours = timeInt % 86400 / 3600
        val minutes = timeInt % 86400 % 3600 / 60
        val seconds = timeInt % 86400 % 3600 % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}