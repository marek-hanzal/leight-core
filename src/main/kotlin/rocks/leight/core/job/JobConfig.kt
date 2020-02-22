package rocks.leight.core.job

data class JobConfig(
        /**
         * maximum number of workers (number of threads in job executor)
         */
        val workers: Int = 16,
        /**
         * queue limit (when job executor throws OverloadException)
         */
        val limit: Int = workers * 4,
        /**
         * if somebody will be queuing in cycle, it could kill the memory; this is
         * hard limit of scheduler queue
         */
        val queueLimit: Int = limit * 4,
        /**
         * how much tickets will cause an erroneous state (meaning whole pipeline is stuck)
         */
        val scheduleLimit: Int = workers + 4,
        /**
         * warning limit of long running jobs (in ms)
         */
        val runtimeThreshold: Long = 5000,
        /**
         * warning when a job has been executed after this amount of time (ms)
         */
        val lateJobThreshold: Long = 5000,
        /**
         * how long should controller sleep when there are no jobs (use with caution as it could
         * at worse ignore new jobs for this amount of time!)
         *
         * use higher times (like 30000 or even higher) if it's uncommon in an application to have
         * background processing and precision is not necessary and lower times if it's required higher
         * job throughput
         *
         * be careful as this setting (0-500) could put a lot of load on database!
         *
         * milliseconds
         */
        val deepSleep: Long = 10000,
        /**
         * when there is no sleep time, how long controller should wait before reschedule
         *
         * use this setting to lower the load on database when there are a lot of parallel jobs as this
         * will put controller asleep for a bit more time, thus whole pipeline will not be overloaded
         *
         * be careful as this setting (0,10,...20) could put a lot of load on database
         *
         * milliseconds
         */
        val shallowSleep: Long = 100
)
