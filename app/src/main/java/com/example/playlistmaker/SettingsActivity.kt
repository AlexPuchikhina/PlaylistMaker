package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.LinearLayout
import android.net.Uri

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val btnBack = findViewById<android.widget.ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
        val themeSwitch = findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switchTheme)
        themeSwitch.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        val btnShare = findViewById<LinearLayout>(R.id.btnShare)
        btnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message))
            }
            startActivity(Intent.createChooser(intent, null))
        }
        val btnSupport = findViewById<LinearLayout>(R.id.btnSupport)
        btnSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_body))
            }
            startActivity(intent)
        }
        val btnAgreement = findViewById<LinearLayout>(R.id.btnAgreement)
        btnAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_link)))
            startActivity(intent)
        }
    }
}