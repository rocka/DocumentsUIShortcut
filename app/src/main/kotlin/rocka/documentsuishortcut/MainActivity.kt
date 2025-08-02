package rocka.documentsuishortcut

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

class MainActivity : Activity() {

    companion object {
        const val TAG = "DocumentsUIShortcut"
        const val STORAGE_URI = "content://com.android.externalstorage.documents/root/primary"
        val DOCUMENTS_UI_PACKAGE = arrayOf(
            "com.android.documentsui", "com.google.android.documentsui"
        )
        const val DOCUMENTS_UI_CLASS = "com.android.documentsui.files.FilesActivity"
    }

    /* fancy, but too bloated
    @Suppress("UNCHECKED_CAST")
    fun <T> Class<*>.get(name: String, default: T): T {
        return try {
            val field = getDeclaredField(name)
            field.isAccessible = true
            field.get(this) as? T ?: default
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            default
        }
    }

    private fun buildUri(): Uri {
        val storageManager = getSystemService(StorageManager::class.java)
        val volume = storageManager.primaryStorageVolume
        val authority = DocumentsContract::class.java.get(
            "EXTERNAL_STORAGE_PROVIDER_AUTHORITY",
            "com.android.externalstorage.documents"
        )
        val rootId = if (volume.isEmulated) {
            DocumentsContract::class.java.get(
                "EXTERNAL_STORAGE_PRIMARY_EMULATED_ROOT_ID",
                "primary"
            )
        } else {
            volume.uuid
        }
        return DocumentsContract.buildRootUri(authority, rootId)
    }
    */

    private fun tryLaunch(intent: Intent, pkg: String): Boolean {
        intent.setClassName(pkg, DOCUMENTS_UI_CLASS)
        try {
            startActivity(intent)
            Log.i(TAG, "Started $pkg")
            return true
        } catch (e: Exception) {
            Log.w(TAG, "Cannot start $pkg: $e")
        }
        return false
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(STORAGE_URI)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
        DOCUMENTS_UI_PACKAGE.firstOrNull {
            tryLaunch(intent, it)
        } ?: run {
            Log.e(TAG, "Cannot start DocumentsUI")
        }
        finish()
    }
}
