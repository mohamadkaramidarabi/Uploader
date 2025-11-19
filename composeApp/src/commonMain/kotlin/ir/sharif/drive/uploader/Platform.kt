package ir.sharif.drive.uploader

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform