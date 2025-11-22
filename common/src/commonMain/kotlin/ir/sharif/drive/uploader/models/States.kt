package ir.sharif.drive.uploader.models


object States  {
    object UploadInfo {
        enum class State {
            IN_QUEUE,
            PREPARING,
            PREPARED,
            STARTING,
            STARTED,
            UPLOADING,
            CANCELED,
            PAUSED,
            ALL_PUT_DONE,
            COMPLETING,
            SUCCESS,
            FAILED;

            companion object {
                operator fun get(name: String) = enumValueOf<State>(name)
            }
        }
    }
    object Link {
        enum class State {
            IN_QUEUE,
            RUNNING,
            SUCCESS,
            FAILED,
            PAUSED;

            companion object {
                operator fun get(name: String) = enumValueOf<State>(name)
            }
        }
    }
}






