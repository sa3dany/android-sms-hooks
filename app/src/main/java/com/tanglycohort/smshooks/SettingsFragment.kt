package com.tanglycohort.smshooks

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import java.io.FileNotFoundException
import java.io.FileOutputStream

/**
 * Uses the Preferences component for managing app settings.
 * Also serves as app home fragment
 */
class SettingsFragment : PreferenceFragmentCompat() {

    /**
     * Simple class that extends [ActivityResultContracts.CreateDocument]
     * Just sets the category (openable) & MIME type to text/plain
     */
    class CreateTextFile : ActivityResultContracts.CreateDocument() {
        override fun createIntent(context: Context, input: String): Intent {
            return super.createIntent(context, input).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setupPreferences()
    }

    private fun setupPreferences() {
        (findPreference("serverUrl")
                as EditTextPreference?)?.apply {
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_TEXT_VARIATION_URI
                editText.hint = getString(R.string.hint_webhook_url)
            }
        }
        (findPreference("exportLogs") as Preference?)?.apply {
            setOnPreferenceClickListener {
                onExportPreferenceClick()
            }
        }
    }

    private fun onExportPreferenceClick(): Boolean {
        registrationToCreateTextFile.launch("smshooks_log.txt")
        return true
    }

    /**
     * After use chooses a location and name to save the log file,
     * we get a file URI as a result
     */
    private fun onCreateTextFileResult(uri: Uri?) {
        try {
            val log = preferenceManager.context.openFileInput("log")
            if (uri != null) {
                activity?.contentResolver?.openFileDescriptor(uri, "w")?.use {
                    FileOutputStream(it.fileDescriptor).use { output ->
                        log?.copyTo(output)
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            // If no log file exists yet (no log entries)
        }
    }

    /**
     * Registration for intent results
     */
    private val registrationToCreateTextFile =
        registerForActivityResult(
            CreateTextFile()
        ) { uri: Uri? -> onCreateTextFileResult(uri) }
}
