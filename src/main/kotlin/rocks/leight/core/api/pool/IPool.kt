package rocks.leight.core.api.pool

import rocks.leight.core.api.config.IConfigurable
import javax.sql.DataSource

interface IPool : IConfigurable<IPool> {
	fun source(): DataSource
}
