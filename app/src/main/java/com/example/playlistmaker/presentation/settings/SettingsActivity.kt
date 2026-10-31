package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
        val app = applicationContext as App
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.switchTheme)
        themeSwitcher.isChecked = app.darkTheme
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            app.switchTheme(checked)
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
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_link)))
            startActivity(intent)
        }
    }
}