export function idbOpen(name, version) {
    return new Promise(function (resolve, reject) {
        const request = window.indexedDB.open(name, version);
        request.onupgradeneeded = function (event) {
            const db = event.target.result;
            if (!db.objectStoreNames.contains("uploads")) {
                db.createObjectStore("uploads", { keyPath: "id" });
            }
            if (!db.objectStoreNames.contains("links")) {
                db.createObjectStore("links", { keyPath: "id" });
            }
            if (!db.objectStoreNames.contains("files")) {
                db.createObjectStore("files", { keyPath: "path" });
            }
            if (!db.objectStoreNames.contains("meta")) {
                db.createObjectStore("meta", { keyPath: "key" });
            }
        };
        request.onsuccess = function () {
            resolve(request.result);
        };
        request.onerror = function () {
            reject(request.error);
        };
    });
}

export function idbSaveJsonRecord(db, storeName, id, payload) {
    return new Promise(function (resolve, reject) {
        const tx = db.transaction(storeName, "readwrite");
        const request = tx.objectStore(storeName).put({ id: id, payload: payload });
        request.onsuccess = function () {
            resolve(null);
        };
        request.onerror = function () {
            reject(request.error);
        };
    });
}

export function idbLoadJsonRecords(db, storeName) {
    return new Promise(function (resolve, reject) {
        const tx = db.transaction(storeName, "readonly");
        const request = tx.objectStore(storeName).getAll();
        request.onsuccess = function () {
            const payloads = (request.result || []).map(function (item) {
                return item.payload;
            });
            resolve(JSON.stringify(payloads));
        };
        request.onerror = function () {
            reject(request.error);
        };
    });
}

export function idbDeleteRecord(db, storeName, key) {
    return new Promise(function (resolve, reject) {
        const tx = db.transaction(storeName, "readwrite");
        const request = tx.objectStore(storeName).delete(key);
        request.onsuccess = function () {
            resolve(null);
        };
        request.onerror = function () {
            reject(request.error);
        };
    });
}

export function idbClearStore(db, storeName) {
    return new Promise(function (resolve, reject) {
        const tx = db.transaction(storeName, "readwrite");
        const request = tx.objectStore(storeName).clear();
        request.onsuccess = function () {
            resolve(null);
        };
        request.onerror = function () {
            reject(request.error);
        };
    });
}

export function idbSaveFile(db, path, fileName, file) {
    return new Promise(function (resolve, reject) {
        const tx = db.transaction("files", "readwrite");
        const request = tx.objectStore("files").put({ path: path, fileName: fileName, blob: file });
        request.onsuccess = function () {
            resolve(null);
        };
        request.onerror = function () {
            reject(request.error);
        };
    });
}

export function idbLoadFileBlob(db, path) {
    return new Promise(function (resolve, reject) {
        const tx = db.transaction("files", "readonly");
        const request = tx.objectStore("files").get(path);
        request.onsuccess = function () {
            resolve(request.result ? request.result.blob : null);
        };
        request.onerror = function () {
            reject(request.error);
        };
    });
}

export function idbLoadFileInfos(db) {
    return new Promise(function (resolve, reject) {
        const tx = db.transaction("files", "readonly");
        const request = tx.objectStore("files").getAll();
        request.onsuccess = function () {
            const items = (request.result || []).map(function (item) {
                return { path: item.path, fileName: item.fileName };
            });
            resolve(JSON.stringify(items));
        };
        request.onerror = function () {
            reject(request.error);
        };
    });
}

export function idbSaveMeta(db, key, value) {
    return new Promise(function (resolve, reject) {
        const tx = db.transaction("meta", "readwrite");
        const request = tx.objectStore("meta").put({ key: key, value: value });
        request.onsuccess = function () {
            resolve(null);
        };
        request.onerror = function () {
            reject(request.error);
        };
    });
}

export function idbLoadMeta(db, key) {
    return new Promise(function (resolve, reject) {
        const tx = db.transaction("meta", "readonly");
        const request = tx.objectStore("meta").get(key);
        request.onsuccess = function () {
            resolve(request.result ? request.result.value : null);
        };
        request.onerror = function () {
            reject(request.error);
        };
    });
}
