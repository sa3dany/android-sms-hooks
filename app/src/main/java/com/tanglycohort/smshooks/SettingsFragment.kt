package com.tanglycohort.smshooks

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.text.InputType
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import java.io.FileNotFoundException

class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
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
    }

    private val registrationToCreateTextFile =
        registerForActivityResult(
            CreateTextFile()
        ) { uri: Uri? -> onCreateTextFileResult(uri) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        if (uri == null)
            return
        preferenceManager.context.also { context ->
            try {
                context.openFileInput("log").use { privateLogFile ->
                    context.contentResolver.openOutputStream(uri)?.use { outputLogFile ->
                        privateLogFile.copyTo(outputLogFile)
                    }
                }
            } catch (e: FileNotFoundException) {
                DocumentsContract.deleteDocument(context.contentResolver, uri)
            }
        }
    }
}
