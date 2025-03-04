package ireader.domain.services.extensions_insstaller_service

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ireader.i18n.R
import ireader.domain.catalogs.interactor.GetInstalledCatalog
import ireader.domain.catalogs.interactor.InstallCatalog
import ireader.domain.catalogs.service.CatalogRemoteRepository
import ireader.domain.notification.NotificationsIds
import ireader.domain.services.downloaderService.DefaultNotificationHelper
import ireader.domain.utils.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class ExtensionManagerService constructor(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params),DIAware {
    override val di: DI = (context.applicationContext as DIAware).di
    private val repository: CatalogRemoteRepository by instance()
    val getInstalledCatalog: GetInstalledCatalog by instance()
    private val installCatalog: InstallCatalog by instance()
    val defaultNotificationHelper: DefaultNotificationHelper by instance()
    private val notificationManager: NotificationManager by instance()
    private val downloadJob = Job()

    val scope = CoroutineScope(Dispatchers.Main.immediate + downloadJob)


    override suspend fun doWork(): Result {
            val builder = defaultNotificationHelper.baseInstallerNotification(
                id
            )
            val result = runExtensionService(
                repository = repository,
                getInstalledCatalog = getInstalledCatalog,
                installCatalog = installCatalog,
                notificationManager = notificationManager,
                updateProgress = { max, progress, inProgess ->
                    builder.setProgress(max,progress,inProgess)
                },
                updateTitle = {
                    builder.setContentTitle(it)
                },
                onCancel = {
                    notificationManager.cancel(NotificationsIds.ID_INSTALLER_PROGRESS)
                    notificationManager.show(NotificationsIds.ID_INSTALLER_ERROR,
                        defaultNotificationHelper.baseInstallerNotification(
                            id,
                            false
                        ).apply {
                            setContentTitle("Installation was stopped.")
                            setOngoing(false)
                        }.build())
                },
                updateNotification = {
                    notificationManager.show(NotificationsIds.ID_INSTALLER_COMPLETE,
                        builder)
                },
                onSuccess = { notInstalled ->
                    notificationManager.cancel(NotificationsIds.ID_INSTALLER_PROGRESS)
                    val notification = NotificationCompat.Builder(
                        applicationContext.applicationContext,
                        NotificationsIds.CHANNEL_INSTALLER_COMPLETE
                    ).apply {
                        setContentTitle("$notInstalled sources was Installed successfully.")
                        setSmallIcon(R.drawable.ic_downloading)
                        priority = NotificationCompat.PRIORITY_DEFAULT
                        setSubText("Installed Successfully")
                        setAutoCancel(true)
                        setContentIntent(defaultNotificationHelper.openDownloadsPendingIntent)
                    }.build()
                    notificationManager.show(NotificationsIds.ID_INSTALLER_COMPLETE,
                        notification)
                }
            )
            return if (result) Result.success() else Result.failure()
    }
}


