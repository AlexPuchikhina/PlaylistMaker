package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.switchTheme)
        viewModel.observeDarkTheme().observe(this) { isDarkTheme ->
            if (themeSwitcher.isChecked != isDarkTheme) {
                themeSwitcher.isChecked = isDarkTheme
            }
            (applicationContext as App).switchTheme(isDarkTheme)
        }
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeSwitched(checked)
        }

        findViewById<LinearLayout>(R.id.btnShare).setOnClickListener {
            viewModel.onShareClicked()
        }
        findViewById<LinearLayout>(R.id.btnSupport).setOnClickListener {
            viewModel.onSupportClicked()
        }
        findViewById<LinearLayout>(R.id.btnAgreement).setOnClickListener {
            viewModel.onUserAgreementClicked()
        }
    }
}