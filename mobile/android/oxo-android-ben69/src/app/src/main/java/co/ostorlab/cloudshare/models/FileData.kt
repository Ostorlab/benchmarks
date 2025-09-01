package co.ostorlab.cloudshare.models

data class FileData(
    val id: Long = 0,
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val mimeType: String,
    val userId: Int,
    val uploadDate: Long = System.currentTimeMillis(),
    val isShared: Boolean = false,
    val shareToken: String? = null
)
