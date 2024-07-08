package sapient.app

import org.koin.dsl.module
import sapient.app.io.QuestDao
import sapient.app.ui.screens.QuestProfileModel

val myModule = module {
    single { ApiClient() }
    single { QuestDao(get()) }
    // supply
    // single { BusService() }
    // models
    // factory { LocationListModel(get(), get(), get()) }
    factory { (id: Int?) -> QuestProfileModel(id, get()) }
}