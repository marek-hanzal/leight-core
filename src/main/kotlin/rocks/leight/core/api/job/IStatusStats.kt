package rocks.leight.core.api.job

interface IStatusStats {
    val created: Int
    val scheduled: Int
    val queued: Int
    val running: Int
    val successful: Int
    val failed: Int
}
