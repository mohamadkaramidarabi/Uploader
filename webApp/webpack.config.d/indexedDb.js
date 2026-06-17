;(function (config) {
    const path = require("path");
    const indexedDbPath = path.resolve(
        __dirname,
        "../../../../api/src/webMain/kotlin/ir/sharif/drive/uploader/cache/indexeddb/indexedDb.mjs",
    );
    config.resolve = config.resolve || {};
    config.resolve.alias = config.resolve.alias || {};
    config.resolve.alias["./indexedDb.mjs"] = indexedDbPath;
})(config);
