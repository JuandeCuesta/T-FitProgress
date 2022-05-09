package edu.juandecuesta.t_fitprogress.calendario

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.juandecuesta.t_fitprogress.databinding.ActivityCalendarioBinding

class CalendarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}