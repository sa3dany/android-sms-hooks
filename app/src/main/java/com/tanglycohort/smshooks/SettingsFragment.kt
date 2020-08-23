package com.tanglycohort.smshooks

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import java.io.FileNotFoundException
import java.io.FileOutputStream

class SettingsFragment : PreferenceFragmentCompat() {

    class CreateTextFile : ActivityResultContracts.CreateDocument() {
        override fun createIntent(context: Context, input: String): Intent {
            return super.createIntent(context, input).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
            }
        }
    }

    private val exportLogs = registerForActivityResult(CreateTextFile()) { uri: Uri? ->
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

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val serverUrlPreference: EditTextPreference? = findPreference("serverUrl")
        serverUrlPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_TEXT_VARIATION_URI
            editText.hint = getString(R.string.settings_server_url_hint)
        }

        val exportLogsPreference: Preference? = findPreference("exportLogs")
        exportLogsPreference?.setOnPreferenceClickListener {
            exportLogs.launch("smshooks-log")
            true
        }
    }
}
