package Database.NotificationDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PanelDao {
    @Upsert
    suspend fun replaceIns(panel: Panel)

    @Delete
    suspend fun remove(panel: Panel)

    @Query("SELECT * FROM Panel WHERE id = :id")
    fun getPanel(id: Int): Flow<Panel>
}
