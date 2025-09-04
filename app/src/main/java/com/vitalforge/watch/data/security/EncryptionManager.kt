package com.vitalforge.watch.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.random.Random

data class EncryptedData(val encryptedData: ByteArray, val iv: ByteArray, val keyAlias: String)

@Singleton
class EncryptionManager(private val context: Context) {
    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val DATABASE_KEY_ALIAS = "vitalforge_db_key"
        private const val DATA_KEY_ALIAS = "vitalforge_data_key"
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val TAG = "EncryptionManager"
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    init {
        createKey(DATABASE_KEY_ALIAS)
        createKey(DATA_KEY_ALIAS)
    }

    private fun createKey(alias: String) {
        if (!keyStore.containsAlias(alias)) {
            val spec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
             .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
             .setKeySize(256)
             .build()
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
                .apply { init(spec) }
                .generateKey()
            Log.d(TAG, "Key created: $alias")
        }
    }

    fun getDatabaseKey(): ByteArray {
        val key = keyStore.getKey(DATABASE_KEY_ALIAS, null) as SecretKey
        return key.encoded
    }

    fun encryptData(plainText: String): EncryptedData {
        val key = keyStore.getKey(DATA_KEY_ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return EncryptedData(encrypted, iv, DATA_KEY_ALIAS)
    }

    fun decryptData(data: EncryptedData): String {
        val key = keyStore.getKey(data.keyAlias, null) as SecretKey
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(128, data.iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        val decrypted = cipher.doFinal(data.encryptedData)
        return String(decrypted, Charsets.UTF_8)
    }

    fun generateSecureToken(): String {
        val bytes = ByteArray(32).apply { Random.nextBytes(this) }
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyHealth(): Boolean {
        return try {
            val test = "health_check"
            val enc = encryptData(test)
            val dec = decryptData(enc)
            dec == test
        } catch (e: Exception) {
            Log.e(TAG, "Health check failed", e)
            false
        }
    }
}
