package com.electroniccode.leafy.datastore

import kotlinx.coroutines.flow.Flow

interface DataStore {

    fun isPhoneUsageAccepted(): Flow<Boolean>

    suspend fun writePhoneUsageValue(value: Boolean)

}