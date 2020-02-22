package rocks.leight.core.api.upgrade

import rocks.leight.core.api.CoreException

class UpgradeException(message: String, cause: Throwable? = null) : CoreException(message, cause)
