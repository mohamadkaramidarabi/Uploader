@file:JsModule("./indexedDb.mjs")
@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.sharif.drive.uploader.cache.indexeddb

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsModule
import kotlin.js.Promise
import org.w3c.files.File

internal external fun idbOpen(name: String, version: Int): Promise<JsAny?>

internal external fun idbSaveJsonRecord(
    db: JsAny,
    storeName: String,
    id: Double,
    payload: String,
): Promise<JsAny?>

internal external fun idbLoadJsonRecords(
    db: JsAny,
    storeName: String,
): Promise<JsAny?>

internal external fun idbDeleteRecord(
    db: JsAny,
    storeName: String,
    key: Double,
): Promise<JsAny?>

internal external fun idbClearStore(
    db: JsAny,
    storeName: String,
): Promise<JsAny?>

internal external fun idbSaveFile(
    db: JsAny,
    path: String,
    fileName: String,
    file: File,
): Promise<JsAny?>

internal external fun idbLoadFileBlob(
    db: JsAny,
    path: String,
): Promise<JsAny?>

internal external fun idbLoadFileInfos(db: JsAny): Promise<JsAny?>

internal external fun idbSaveMeta(
    db: JsAny,
    key: String,
    value: Double,
): Promise<JsAny?>

internal external fun idbLoadMeta(
    db: JsAny,
    key: String,
): Promise<JsAny?>
