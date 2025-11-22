package ir.sharif.drive.uploader.network

object EndPoints {
    private const val V1 = "/api/v1"
    private const val V2 = "/api/v2"
    
    object Authentication {
        const val generateCode = "/api/v6/profile/auth/generate-code/"
        const val login = "/api/v6/profile/auth/login/"
        const val validateOtp = "/api/v6/profile/auth/validate-otp/"
        const val refreshToken = "/api/v2/profile/auth/token-refresh/"
    }
    
    object Upload {
        const val startUpload = "$V2/flat/start-upload/"
        const val completeUpload = "$V2/flat/complete-upload/"
        const val abortUpload = "$V2/flat/abort-upload/"
    }
}

