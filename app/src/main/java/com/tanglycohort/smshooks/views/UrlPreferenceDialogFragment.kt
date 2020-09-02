package com.tanglycohort.smshooks.views

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputLayout
import com.tanglycohort.smshooks.R
import okhttp3.HttpUrl.Companion.toHttpUrl

class UrlPreferenceDialogFragment() : DialogFragment() {
    private val args: UrlPreferenceDialogFragmentArgs by navArgs()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var positiveButton: Button
    private lateinit var editTextLayout: TextInputLayout

    private var preferenceText: String?
        get() = getPreference()
        set(value) = setPreference(value)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder

        requireActivity().apply {
            layoutInflater.inflate(R.layout.dialog_edit_text, null).also { view ->
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                editTextLayout = view.findViewById(R.id.editTextLayout)
                builder = AlertDialog.Builder(this)
                    .setView(view)
                    .setPositiveButton("Save") { _, _ -> onPositiveButtonClick() }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            }
        }

        editTextLayout.apply {
            addOnEditTextAttachedListener { onEditTextAttached(it) }
            editTextLayout.prefixText = (if (args.forceHttps) "https://" else null)
        }

        return builder.create().also {
            it.setOnShowListener { dialog -> onDialogShow(dialog) }
        }
    }

    private fun onDialogShow(dialog: DialogInterface) {
        positiveButton = (dialog as AlertDialog).getButton(BUTTON_POSITIVE)
        positiveButton.isEnabled = false
    }

    private fun onEditTextAttached(textInputLayout: TextInputLayout) {
        textInputLayout.hint = getString(R.string.preference_webhook_url)
        textInputLayout.editText?.apply {
            if (args.forceHttps && preferenceText!!.startsWith("https://")) {
                setText(preferenceText!!.drop(8))
            } else {
                setText(preferenceText)
            }
            inputType = InputType.TYPE_TEXT_VARIATION_URI
            doAfterTextChanged { editable -> onEditTextChanged(editable) }
        }
    }

    private fun onEditTextChanged(editable: Editable?) {
        if (isValidUrl(editable.toString())) {
            setError(null)
        } else {
            setError("Invalid URL")
        }
    }

    private fun onPositiveButtonClick() {
        preferenceText = editTextLayout.editText?.text.toString()
    }

    private fun setError(message: String?) {
        positiveButton.isEnabled = message == null
        editTextLayout.error = message
    }

    private fun getPreference(): String? {
        return if (::sharedPreferences.isInitialized) {
            return sharedPreferences.getString(args.key, null)
        } else {
            null
        }
    }

    private fun setPreference(newValue: String?) {
        if (::sharedPreferences.isInitialized)
            sharedPreferences.edit().putString(
                args.key,
                if (args.forceHttps) "https://$newValue" else newValue
            ).apply()
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            true.also {
                if (args.forceHttps) "https://$url".toHttpUrl() else url.toHttpUrl()
            }
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}