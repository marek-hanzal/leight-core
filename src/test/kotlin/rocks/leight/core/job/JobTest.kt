package rocks.leight.core.job

//class JobTest {
//    @Test
//    fun `job flow`() {
//        ContainerFactory.container().apply {
//            /**
//             * the boring pre-test stuff
//             */
//            register(PoolConfig::class) { ConfigFactory.load().extract("core.pool") }
//            register(JobConfig::class) { JobConfig(workers = 1, limit = 4, deepSleep = 100) }
//            configurator(IMessageBus::class) { register(MessageService::class) }
//            configurator(IUpgradeManager::class) { register(JobUpgrade::class) }
//            create(IStorage::class).apply {
//                setup()
//                transaction { SchemaUtils.drop(UpgradeTable, JobTable) }
//            }
//            create(IUpgradeManager::class).apply { setup(); upgrade() }
//            /**
//             * let's rock bitch!
//             */
//            val jobExecutor = create(IJobExecutor::class)
//            val storage = create(IStorage::class)
//            create(IJobManager::class).apply {
//                start()
//                var index = 0
//                storage.write {
//                    repeat(8) {
//                        schedule(DummyMessage(250), 250 * index++)
//                    }
//                }
//                Thread.sleep(2500)
//                assertEquals(0, jobExecutor.count())
//                assertTrue(stop())
//                assertEquals(0, jobExecutor.count())
//            }
//        }
//    }
//}
