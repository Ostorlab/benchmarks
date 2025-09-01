package co.ostorlab.cloudshare.models

data class ShareData(
    val id: Long = 0,
    val token: String,
    val shareWith: String,  // bcrypt password hash
    val fileName: String,
    val fileSize: Long,
    val userId: Int,
    val expiration: Long,
    val shareUrl: String,
    val created: Long = System.currentTimeMillis(),
    val downloadCount: Int = 0,
    val isActive: Boolean = true
)
