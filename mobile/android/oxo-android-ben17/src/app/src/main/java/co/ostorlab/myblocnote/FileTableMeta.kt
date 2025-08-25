package co.ostorlab.myblocnote

object FileTableMeta {
    // Table des fichiers
    const val FILE_TABLE_NAME = "files"
    const val _ID = "_id"
    const val FILE_NAME = "name"
    const val FILE_PATH = "path"
    const val FILE_SIZE = "size"
    
    const val CREATE_TABLE_FILES = """
        CREATE TABLE $FILE_TABLE_NAME (
            $_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $FILE_NAME TEXT NOT NULL,
            $FILE_PATH TEXT NOT NULL,
            $FILE_SIZE INTEGER
        )
    """

    // Table des capacités
    const val CAPABILITIES_TABLE_NAME = "capabilities"
    const val CAPABILITIES_ID = "cap_id"
    const val CAPABILITIES_DATA = "data"
    
    const val CREATE_TABLE_CAPABILITIES = """
        CREATE TABLE $CAPABILITIES_TABLE_NAME (
            $CAPABILITIES_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $CAPABILITIES_DATA TEXT
        )
    """

    // Table des uploads
    const val UPLOADS_TABLE_NAME = "uploads"
    const val UPLOAD_ID = "upload_id"
    const val UPLOAD_STATUS = "status"
    
    const val CREATE_TABLE_UPLOADS = """
        CREATE TABLE $UPLOADS_TABLE_NAME (
            $UPLOAD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $UPLOAD_STATUS INTEGER
        )
    """

    // Table de synchronisation des uploads caméra
    const val CAMERA_UPLOADS_SYNC_TABLE_NAME = "camera_uploads_sync"
    const val SYNC_ID = "sync_id"
    const val SYNC_TIMESTAMP = "timestamp"
    
    const val CREATE_TABLE_CAMERA_UPLOADS_SYNC = """
        CREATE TABLE $CAMERA_UPLOADS_SYNC_TABLE_NAME (
            $SYNC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $SYNC_TIMESTAMP INTEGER
        )
    """

    // Table des quotas utilisateurs
    const val USER_QUOTAS_TABLE_NAME = "user_quotas"
    const val QUOTA_ID = "quota_id"
    const val QUOTA_VALUE = "value"
    
    const val CREATE_TABLE_USER_QUOTAS = """
        CREATE TABLE $USER_QUOTAS_TABLE_NAME (
            $QUOTA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $QUOTA_VALUE INTEGER
        )
    """
}
