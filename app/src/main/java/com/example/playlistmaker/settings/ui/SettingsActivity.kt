package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory()
        )[SettingsViewModel::class.java]

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