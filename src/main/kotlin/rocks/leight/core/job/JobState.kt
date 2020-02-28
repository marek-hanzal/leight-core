package rocks.leight.core.job

enum class JobState {
    CREATED,
    SCHEDULED,
    QUEUED,
    WAITING,
    RUNNING,
    SUCCESSFUL,
    FAILED,
}
