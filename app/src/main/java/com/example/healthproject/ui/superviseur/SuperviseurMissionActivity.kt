package com.example.healthproject.ui.superviseur

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthproject.R
import com.example.healthproject.ui.superviseur.presence.PresenceFragment

class SuperviseurMissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_superviseur)

        val missionId = intent.getStringExtra("missionId")!!

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PresenceFragment.newInstance(missionId))
            .commit()
    }
}
