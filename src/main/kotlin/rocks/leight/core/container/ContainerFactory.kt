package rocks.leight.core.container

import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.job.*
import rocks.leight.core.api.message.IMessageBus
import rocks.leight.core.api.pool.IPool
import rocks.leight.core.api.rest.discovery.IDiscoveryService
import rocks.leight.core.api.rest.page.IPageService
import rocks.leight.core.api.scraper.IScraper
import rocks.leight.core.api.server.IHttpServer
import rocks.leight.core.api.server.ILinkGenerator
import rocks.leight.core.api.storage.IStorage
import rocks.leight.core.api.upgrade.IUpgradeManager
import rocks.leight.core.api.upgrade.IVersionService
import rocks.leight.core.job.JobController
import rocks.leight.core.job.JobExecutor
import rocks.leight.core.job.JobManager
import rocks.leight.core.job.JobScheduler
import rocks.leight.core.job.stats.JobStats
import rocks.leight.core.message.MessageBus
import rocks.leight.core.pool.Pool
import rocks.leight.core.rest.discovery.DiscoveryService
import rocks.leight.core.rest.page.PageService
import rocks.leight.core.scraper.Scraper
import rocks.leight.core.server.HttpServer
import rocks.leight.core.server.LinkGenerator
import rocks.leight.core.storage.Storage
import rocks.leight.core.upgrade.UpgradeManager
import rocks.leight.core.upgrade.VersionService
import kotlin.time.ExperimentalTime

@ExperimentalTime
object ContainerFactory {
	fun container() = Container().apply {
		registerSystemServices()
		registerStorageServices()
		registerJobServices()
		registerHttpServices()
	}

	private fun IContainer.registerSystemServices() {
		register(IContainer::class) { this }
		register(IUpgradeManager::class, UpgradeManager::class)
		register(IVersionService::class, VersionService::class)
		register(IMessageBus::class, MessageBus::class)
	}

	private fun IContainer.registerStorageServices() {
		register(IStorage::class, Storage::class)
		register(IPool::class, Pool::class)
	}

	private fun IContainer.registerJobServices() {
		register(IJobScheduler::class, JobScheduler::class)
		register(IJobManager::class, JobManager::class)
		register(IJobExecutor::class, JobExecutor::class)
		register(IJobController::class, JobController::class)
		register(IJobStats::class, JobStats::class)
	}

	private fun IContainer.registerHttpServices() {
		register(IHttpServer::class, HttpServer::class)
		register(ILinkGenerator::class, LinkGenerator::class)
		register(IPageService::class, PageService::class)
		register(IDiscoveryService::class, DiscoveryService::class)
		register(IScraper::class, Scraper::class)
	}
}
