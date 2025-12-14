package Database.NotificationDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Panel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "repeat") val repeat: Boolean?,
    @ColumnInfo(name = "notify") val notify: Boolean?,
    @ColumnInfo(name = "time") val time: String?
)
