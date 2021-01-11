package com.electroniccode.leafy.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.createDataStore
import com.electroniccode.leafy.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreHelper constructor(context: Context) : DataStore {

    private val datastore = context.createDataStore(
        name = Constants.DATASTORE_NAME
    )

    override fun isPhoneUsageAccepted() = datastore.data.catch {
        if(it is IOException) {
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { it[Constants.DATASTORE_PHONE_KEY] ?: false }

    override suspend fun writePhoneUsageValue(value: Boolean) {
        datastore.edit {
            it[Constants.DATASTORE_PHONE_KEY] = value
        }
    }
}