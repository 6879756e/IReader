package ir.kazemcodes.infinity.feature_sources.presentation.extension

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.kazemcodes.infinity.core.data.network.models.Source
import ir.kazemcodes.infinity.core.domain.repository.LocalSourceRepository
import ir.kazemcodes.infinity.core.utils.Resource
import ir.kazemcodes.infinity.core.utils.UiEvent
import ir.kazemcodes.infinity.feature_sources.sources.Extensions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtensionViewModel @Inject constructor(private val localSourceRepository: LocalSourceRepository,private val extensions: Extensions) :
    ViewModel() {

    private val _state =
        mutableStateOf(ExtensionScreenState())

    val state: State<ExtensionScreenState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    init {
        getSources()
    }


    fun updateSource(sources: List<Source>) {
        _state.value = state.value.copy(sources = sources)
    }

    fun getSources() {
        coroutineScope.launch(Dispatchers.IO) {
            localSourceRepository.getSources().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data != null) {
                            _state.value =
                                state.value.copy(sources = result.data + extensions.getSources())
                            result.data.forEach {
                                extensions.addSource(it)
                            }
                        }
                    }
                    is Resource.Error -> {

                    }


                }
            }.launchIn(coroutineScope)


        }
    }


}

data class ExtensionScreenState(
    val sources: List<Source> = emptyList(),
    val communitySources : List<Source> = emptyList()
)