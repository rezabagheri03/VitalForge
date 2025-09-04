package com.vitalforge.watch.data.database

import android.content.Context
import androidx.room.Room
import com.vitalforge.watch.data.security.EncryptionManager
import net.sqlcipher.database.SupportFactory

object DatabaseBuilder {
    @Volatile
    private var INSTANCE: VitalForgeDatabase? = null

    fun getDatabase(context: Context): VitalForgeDatabase {
        return INSTANCE ?: synchronized(this) {
            val passphrase = EncryptionManager(context).getDatabaseKey()
            val factory = SupportFactory(passphrase.toByteArray())
            val instance = Room.databaseBuilder(
                context.applicationContext,
                VitalForgeDatabase::class.java,
                "vitalforge_encrypted.db"
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }
}
