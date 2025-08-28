package co.ostorlab.myapplication

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File
import java.io.FileNotFoundException

class PhotoProvider : ContentProvider() {
    
    companion object {
        private const val AUTHORITY = "co.ostorlab.myapplication.photos"
        private const val PHOTOS = 1
        private const val PHOTO_ID = 2
        
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "photos", PHOTOS)
            addURI(AUTHORITY, "photos/#", PHOTO_ID)
        }
    }
    
    override fun onCreate(): Boolean {
        Log.d("PhotoProvider", "PhotoProvider initialized - exposing photos with GPS metadata")
        return true
    }
    
    // VULNERABLE: Returns all photos without permission checks
    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        
        Log.d("PhotoProvider", "Query request from external app: $uri")
        
        val cursor = MatrixCursor(arrayOf("_id", "file_path", "timestamp", "has_gps"))
        
        when (uriMatcher.match(uri)) {
            PHOTOS -> {
                // VULNERABLE: Return all photos without access control
                val picturesDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                picturesDir?.listFiles()?.filter { it.name.endsWith(".jpg") }?.forEachIndexed { index, file ->
                    cursor.addRow(arrayOf(
                        index + 1,
                        file.absolutePath,
                        file.lastModified(),
                        "true"  // Indicates GPS data is embedded
                    ))
                }
                
                Log.d("PhotoProvider", "Exposing ${cursor.count} photos with GPS metadata to external app")
            }
            
            PHOTO_ID -> {
                // Return specific photo info
                val photoId = uri.lastPathSegment?.toIntOrNull() ?: return null
                val picturesDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val photos = picturesDir?.listFiles()?.filter { it.name.endsWith(".jpg") }
                
                if (photos != null && photoId > 0 && photoId <= photos.size) {
                    val photo = photos[photoId - 1]
                    cursor.addRow(arrayOf(
                        photoId,
                        photo.absolutePath,
                        photo.lastModified(),
                        "true"
                    ))
                }
            }
        }
        
        return cursor
    }
    
    // VULNERABLE: Opens photo files without permission checks
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        Log.d("PhotoProvider", "File access request from external app: $uri")
        
        when (uriMatcher.match(uri)) {
            PHOTO_ID -> {
                val photoId = uri.lastPathSegment?.toIntOrNull() ?: throw FileNotFoundException("Invalid photo ID")
                val picturesDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    ?: throw FileNotFoundException("Pictures directory not found")
                
                val photos = picturesDir.listFiles()?.filter { it.name.endsWith(".jpg") }
                    ?: throw FileNotFoundException("No photos found")
                
                if (photoId > 0 && photoId <= photos.size) {
                    val photoFile = photos[photoId - 1]
                    
                    Log.d("PhotoProvider", "🚨 EXPOSING PHOTO WITH GPS: ${photoFile.absolutePath}")
                    
                    // VULNERABLE: No permission check - any app can access photos with GPS data
                    return ParcelFileDescriptor.open(photoFile, ParcelFileDescriptor.MODE_READ_ONLY)
                }
            }
            
            PHOTOS -> {
                // Allow access to specific file path
                val filePath = uri.getQueryParameter("path")
                if (filePath != null) {
                    val file = File(filePath)
                    if (file.exists() && file.parent?.contains("Pictures") == true) {
                        Log.d("PhotoProvider", "🚨 EXPOSING PHOTO BY PATH: $filePath")
                        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                    }
                }
            }
        }
        
        throw FileNotFoundException("Photo not found: $uri")
    }
    
    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            PHOTOS -> "vnd.android.cursor.dir/vnd.co.ostorlab.myapplication.photo"
            PHOTO_ID -> "image/jpeg"
            else -> null
        }
    }
    
    // Not implemented - read-only provider
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
}
