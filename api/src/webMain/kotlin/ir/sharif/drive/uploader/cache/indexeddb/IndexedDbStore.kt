@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.sharif.drive.uploader.cache.indexeddb

import ir.sharif.drive.uploader.cache.entity.WebLinkEntity
import ir.sharif.drive.uploader.cache.entity.WebUploadEntity
import ir.sharif.drive.uploader.models.States
import kotlin.js.JsAny
import kotlin.js.Promise
import kotlinx.coroutines.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.w3c.files.Blob
import org.w3c.files.File
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@Serializable
internal data class StoredUpload(
    val id: Long,
    val serverId: String? = null,
    val key: String? = null,
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val chunkSize: Long? = null,
    val chunkCount: Int? = null,
    val state: String,
    val folderId: String? = null,
    val versionGroup: String? = null,
    val cloudPath: String,
    val metaData: String? = null,
)

@Serializable
internal data class StoredLink(
    val id: Long,
    val uploadId: Long,
    val eTag: String? = null,
    val state: String,
    val retryCount: Int,
    val chunkSize: Long,
    val url: String,
)

@Serializable
internal data class StoredBlobInfo(
    val path: String,
    val fileName: String,
)

internal val indexedDbJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

internal fun WebUploadEntity.toStored(): StoredUpload = StoredUpload(
    id = id,
    serverId = serverId,
    key = key,
    fileName = fileName,
    filePath = filePath,
    fileSize = fileSize,
    chunkSize = chunkSize,
    chunkCount = chunkCount,
    state = state.name,
    folderId = folderId,
    versionGroup = versionGroup,
    cloudPath = cloudPath,
    metaData = metaData,
)

internal fun StoredUpload.toEntity(): WebUploadEntity = WebUploadEntity(
    id = id,
    serverId = serverId,
    key = key,
    fileName = fileName,
    filePath = filePath,
    fileSize = fileSize,
    chunkSize = chunkSize,
    chunkCount = chunkCount,
    state = States.UploadInfo.State.valueOf(state),
    folderId = folderId,
    versionGroup = versionGroup,
    cloudPath = cloudPath,
    metaData = metaData,
)

internal fun WebLinkEntity.toStored(): StoredLink = StoredLink(
    id = id,
    uploadId = uploadId,
    eTag = eTag,
    state = state.name,
    retryCount = retryCount,
    chunkSize = chunkSize,
    url = url,
)

internal fun StoredLink.toEntity(): WebLinkEntity = WebLinkEntity(
    id = id,
    uploadId = uploadId,
    eTag = eTag,
    state = States.Link.State.valueOf(state),
    retryCount = retryCount,
    chunkSize = chunkSize,
    url = url,
)

private const val DB_NAME = "uploader-db"
private const val DB_VERSION = 1

private var database: JsAny? = null

@Suppress("UNUSED_PARAMETER")
private val idbOpen: (String, Int) -> Promise<JsAny?> = js(
    """
    function(name, version) {
      return new Promise(function(resolve, reject) {
        var request = window.indexedDB.open(name, version);
        request.onupgradeneeded = function(event) {
          var db = event.target.result;
          if (!db.objectStoreNames.contains('uploads')) {
            db.createObjectStore('uploads', { keyPath: 'id' });
          }
          if (!db.objectStoreNames.contains('links')) {
            db.createObjectStore('links', { keyPath: 'id' });
          }
          if (!db.objectStoreNames.contains('files')) {
            db.createObjectStore('files', { keyPath: 'path' });
          }
          if (!db.objectStoreNames.contains('meta')) {
            db.createObjectStore('meta', { keyPath: 'key' });
          }
        };
        request.onsuccess = function() { resolve(request.result); };
        request.onerror = function() { reject(request.error); };
      });
    }
    """
)

@Suppress("UNUSED_PARAMETER")
private val idbSaveJsonRecord: (JsAny, String, Double, String) -> Promise<JsAny?> = js(
    """
    function(db, storeName, id, payload) {
      return new Promise(function(resolve, reject) {
        var tx = db.transaction(storeName, 'readwrite');
        var request = tx.objectStore(storeName).put({ id: id, payload: payload });
        request.onsuccess = function() { resolve(null); };
        request.onerror = function() { reject(request.error); };
      });
    }
    """
)

@Suppress("UNUSED_PARAMETER")
private val idbLoadJsonRecords: (JsAny, String) -> Promise<JsAny?> = js(
    """
    function(db, storeName) {
      return new Promise(function(resolve, reject) {
        var tx = db.transaction(storeName, 'readonly');
        var request = tx.objectStore(storeName).getAll();
        request.onsuccess = function() {
          var payloads = (request.result || []).map(function(item) { return item.payload; });
          resolve(JSON.stringify(payloads));
        };
        request.onerror = function() { reject(request.error); };
      });
    }
    """
)

@Suppress("UNUSED_PARAMETER")
private val idbDeleteRecord: (JsAny, String, Double) -> Promise<JsAny?> = js(
    """
    function(db, storeName, key) {
      return new Promise(function(resolve, reject) {
        var tx = db.transaction(storeName, 'readwrite');
        var request = tx.objectStore(storeName).delete(key);
        request.onsuccess = function() { resolve(null); };
        request.onerror = function() { reject(request.error); };
      });
    }
    """
)

@Suppress("UNUSED_PARAMETER")
private val idbClearStore: (JsAny, String) -> Promise<JsAny?> = js(
    """
    function(db, storeName) {
      return new Promise(function(resolve, reject) {
        var tx = db.transaction(storeName, 'readwrite');
        var request = tx.objectStore(storeName).clear();
        request.onsuccess = function() { resolve(null); };
        request.onerror = function() { reject(request.error); };
      });
    }
    """
)

@Suppress("UNUSED_PARAMETER")
private val idbSaveFile: (JsAny, String, String, File) -> Promise<JsAny?> = js(
    """
    function(db, path, fileName, file) {
      return new Promise(function(resolve, reject) {
        var tx = db.transaction('files', 'readwrite');
        var request = tx.objectStore('files').put({ path: path, fileName: fileName, blob: file });
        request.onsuccess = function() { resolve(null); };
        request.onerror = function() { reject(request.error); };
      });
    }
    """
)

@Suppress("UNUSED_PARAMETER")
private val idbLoadFileBlob: (JsAny, String) -> Promise<JsAny?> = js(
    """
    function(db, path) {
      return new Promise(function(resolve, reject) {
        var tx = db.transaction('files', 'readonly');
        var request = tx.objectStore('files').get(path);
        request.onsuccess = function() {
          resolve(request.result ? request.result.blob : null);
        };
        request.onerror = function() { reject(request.error); };
      });
    }
    """
)

@Suppress("UNUSED_PARAMETER")
private val idbLoadFileInfos: (JsAny) -> Promise<JsAny?> = js(
    """
    function(db) {
      return new Promise(function(resolve, reject) {
        var tx = db.transaction('files', 'readonly');
        var request = tx.objectStore('files').getAll();
        request.onsuccess = function() {
          var items = (request.result || []).map(function(item) {
            return { path: item.path, fileName: item.fileName };
          });
          resolve(JSON.stringify(items));
        };
        request.onerror = function() { reject(request.error); };
      });
    }
    """
)

@Suppress("UNUSED_PARAMETER")
private val idbSaveMeta: (JsAny, String, Double) -> Promise<JsAny?> = js(
    """
    function(db, key, value) {
      return new Promise(function(resolve, reject) {
        var tx = db.transaction('meta', 'readwrite');
        var request = tx.objectStore('meta').put({ key: key, value: value });
        request.onsuccess = function() { resolve(null); };
        request.onerror = function() { reject(request.error); };
      });
    }
    """
)

@Suppress("UNUSED_PARAMETER")
private val idbLoadMeta: (JsAny, String) -> Promise<JsAny?> = js(
    """
    function(db, key) {
      return new Promise(function(resolve, reject) {
        var tx = db.transaction('meta', 'readonly');
        var request = tx.objectStore('meta').get(key);
        request.onsuccess = function() {
          resolve(request.result ? request.result.value : null);
        };
        request.onerror = function() { reject(request.error); };
      });
    }
    """
)

internal object IndexedDbStore {
    private fun db(): JsAny = database ?: error("IndexedDB is not open")

    suspend fun open() {
        if (database != null) return
        database = idbOpen(DB_NAME, DB_VERSION).await()
    }

    suspend fun loadUploads(): List<WebUploadEntity> {
        open()
        return decodePayloads<StoredUpload>(idbLoadJsonRecords(db(), "uploads").await().asJsonString()).map { it.toEntity() }
    }

    suspend fun loadLinks(): List<WebLinkEntity> {
        open()
        return decodePayloads<StoredLink>(idbLoadJsonRecords(db(), "links").await().asJsonString()).map { it.toEntity() }
    }

    suspend fun saveUpload(upload: WebUploadEntity) {
        open()
        idbSaveJsonRecord(
            db(),
            "uploads",
            upload.id.toDouble(),
            indexedDbJson.encodeToString(upload.toStored()),
        ).await()
    }

    suspend fun saveLink(link: WebLinkEntity) {
        open()
        idbSaveJsonRecord(
            db(),
            "links",
            link.id.toDouble(),
            indexedDbJson.encodeToString(link.toStored()),
        ).await()
    }

    suspend fun deleteLink(linkId: Long) {
        open()
        idbDeleteRecord(db(), "links", linkId.toDouble()).await()
    }

    suspend fun clearUploadsAndLinks() {
        open()
        idbClearStore(db(), "uploads").await()
        idbClearStore(db(), "links").await()
    }

    suspend fun saveFile(path: String, fileName: String, file: File) {
        open()
        idbSaveFile(db(), path, fileName, file).await()
    }

    suspend fun loadFileBlob(path: String): Blob? {
        open()
        return idbLoadFileBlob(db(), path).await() as? Blob
    }

    suspend fun loadAllFileInfos(): List<StoredBlobInfo> {
        open()
        return indexedDbJson.decodeFromString(idbLoadFileInfos(db()).await().asJsonString())
    }

    suspend fun clearFiles() {
        open()
        idbClearStore(db(), "files").await()
    }

    suspend fun getMetaLong(key: String, default: Long): Long {
        open()
        return idbLoadMeta(db(), key).await().asDouble()?.toLong() ?: default
    }

    suspend fun saveMetaLong(key: String, value: Long) {
        open()
        idbSaveMeta(db(), key, value.toDouble()).await()
    }

    private inline fun <reified T> decodePayloads(jsonArray: String): List<T> {
        val payloads = indexedDbJson.decodeFromString<List<String>>(jsonArray)
        return payloads.map { indexedDbJson.decodeFromString<T>(it) }
    }

    private fun JsAny?.asJsonString(): String = this?.toString() ?: "[]"

    private fun JsAny?.asDouble(): Double? = this?.toString()?.toDoubleOrNull()
}
