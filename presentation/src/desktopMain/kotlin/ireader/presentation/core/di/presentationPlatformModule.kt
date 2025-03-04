package ireader.presentation.core.di

import ireader.core.http.WebViewManger
import ireader.presentation.core.PlatformHelper
import ireader.presentation.core.theme.IUseController
import ireader.presentation.core.theme.LocaleHelper
import ireader.presentation.ui.reader.viewmodel.PlatformReaderSettingReader
import org.kodein.di.DI
import org.kodein.di.bindSingleton

actual val presentationPlatformModule: DI.Module = DI.Module("desktopPresentationModule") {
    bindSingleton { LocaleHelper() }
    bindSingleton { PlatformHelper() }
    bindSingleton<PlatformReaderSettingReader> { PlatformReaderSettingReader() }
    bindSingleton { WebViewManger() }
    bindSingleton<IUseController> { IUseController() }
}