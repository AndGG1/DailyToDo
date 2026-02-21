package Database.NotificationDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Panel::class],
    version = 2
)
abstract class PanelDatabase: RoomDatabase() {
    abstract fun panelDao() : PanelDao
}
