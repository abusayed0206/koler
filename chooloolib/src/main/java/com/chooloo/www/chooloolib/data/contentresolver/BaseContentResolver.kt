package com.chooloo.www.chooloolib.data.contentresolver

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import com.chooloo.www.chooloolib.di.module.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

abstract class BaseContentResolver<ItemType>(
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private var _filter: String? = null

    val finalUri: Uri
        get() = _filter.let { filter ->
            if (filterUri != null && filter?.isNotEmpty() == true) {
                Uri.withAppendedPath(filterUri, Uri.encode(filter))
            } else {
                uri
            }
        }

    open var filter: String?
        get() = _filter
        set(value) {
            _filter = if (value == "") null else value
        }

    /** Queries and fully reads the cursor; the cursor never outlives this call. */
    suspend fun queryItems(): ArrayList<ItemType> = withContext(ioDispatcher) {
        contentResolver.query(
            finalUri,
            projection,
            if (selection == "") null else selection,
            selectionArgs?.takeIf { it.isNotEmpty() },
            if (sortOrder == "") null else sortOrder
        )?.use(::convertCursorToItems) ?: ArrayList()
    }

    /** Emits the items now and again on every content change, coalescing changes that arrive mid-query. */
    fun getItemsFlow(): Flow<ArrayList<ItemType>> = callbackFlow {
        val callback = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                trySend(Unit)
            }
        }
        contentResolver.registerContentObserver(finalUri, false, callback)
        trySend(Unit)
        awaitClose { contentResolver.unregisterContentObserver(callback) }
    }.buffer(Channel.CONFLATED).map { queryItems() }

    private fun convertCursorToItems(cursor: Cursor): ArrayList<ItemType> {
        val content = ArrayList<ItemType>()
        while (cursor.moveToNext()) {
            content.add(convertCursorToItem(cursor))
        }
        return content
    }

    abstract val uri: Uri
    abstract val filterUri: Uri?
    abstract val selection: String?
    abstract val sortOrder: String?
    abstract val projection: Array<String>
    abstract val selectionArgs: Array<String>?
    abstract fun convertCursorToItem(cursor: Cursor): ItemType
}
