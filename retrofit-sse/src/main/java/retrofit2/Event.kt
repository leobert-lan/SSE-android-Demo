package retrofit2

/**
 * Classname: Event
 * Created by Leobert on 2023/11/27.
 */
data class Event(val id: String?, val type: String?, val data: String, val thr: Throwable? = null) {
    val failed: Boolean = thr != null
}