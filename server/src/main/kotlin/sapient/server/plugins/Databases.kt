package sapient.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:./test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )

    transaction(database) {
        // SchemaUtils.create(UserTable)
        // SchemaUtils.create(LocationTable)
        // SchemaUtils.create(AreaTable)
    }

    environment.monitor.subscribe(ApplicationStarted) {
        launch {
            // make json backup
//            val userCount = UserService().readAll().size
//            if (userCount == 0) {
//                println("backup restored")
//                DbBackup.restore()
//            } else {
//                println("backup created")
//                DbBackup.create()
//            }
        }
    }
}