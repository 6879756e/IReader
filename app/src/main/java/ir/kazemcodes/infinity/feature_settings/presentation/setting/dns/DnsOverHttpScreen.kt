package ir.kazemcodes.infinity.feature_settings.presentation.setting.dns

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ir.kazemcodes.infinity.core.data.network.models.dnsOverHttps
import ir.kazemcodes.infinity.core.data.network.utils.toast
import ir.kazemcodes.infinity.core.presentation.reusable_composable.TopAppBarBackButton
import ir.kazemcodes.infinity.core.presentation.reusable_composable.TopAppBarTitle
import ir.kazemcodes.infinity.core.utils.Constants
import ir.kazemcodes.infinity.feature_library.presentation.components.RadioButtonWithTitleComposable
import ir.kazemcodes.infinity.feature_settings.presentation.setting.SettingViewModel

@Composable
fun DnsOverHttpScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: SettingViewModel = hiltViewModel(),
) {

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TopAppBarTitle(title = "DnsOverHttp")
                },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                elevation = Constants.DEFAULT_ELEVATION,
                navigationIcon = { TopAppBarBackButton(navController = navController) }
            )
        },
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
            dnsOverHttps.forEach {
                RadioButtonWithTitleComposable(text = it.title,
                    selected = viewModel.state.value.doh == it.prefCode,
                    onClick = {
                        viewModel.setDohPrfUpdate(it.prefCode)
                        context.toast("relaunch the app to make this take effect")
                    })
            }
        }


    }

}