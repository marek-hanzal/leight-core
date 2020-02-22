package rocks.leight.core.pool

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.pool.IPool
import rocks.leight.core.config.AbstractConfigurable
import javax.sql.DataSource

class Pool(container: IContainer) : AbstractConfigurable<IPool>(), IPool {
    private val poolConfig: PoolConfig by container.lazy()
    private lateinit var dataSource: DataSource

    override fun source(): DataSource {
        return dataSource
    }

    override fun onSetup() {
        super.onSetup()
        dataSource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = poolConfig.url
            username = poolConfig.user
            password = poolConfig.password
            maximumPoolSize = poolConfig.size
            poolName = poolConfig.name
        })
    }
}
