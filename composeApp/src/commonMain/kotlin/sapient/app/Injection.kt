package sapient.app

import org.koin.dsl.module

val myModule = module {
    single { ApiClient() }
    // supply
    // single { BusService() }
    // models
    // factory { LocationListModel(get(), get(), get()) }
}