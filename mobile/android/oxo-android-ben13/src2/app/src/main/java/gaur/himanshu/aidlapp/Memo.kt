package gaur.himanshu.aidlapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Memo(
    val id: Int,
    val title: String,
    val content: String
): Parcelable
