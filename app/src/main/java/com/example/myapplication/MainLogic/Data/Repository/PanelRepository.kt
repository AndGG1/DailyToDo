package com.example.myapplication.MainLogic.Data.Repository

import Database.NotificationDB.Panel
import Database.NotificationDB.PanelDatabase
import android.content.Context
import androidx.room.Room

class PanelRepository(appContext: Context) {
   private val db = Room.databaseBuilder(
        context = appContext.applicationContext,
          PanelDatabase::class.java, "panel-database"
    ).build()

   suspend fun modifyPanel(panel: Panel) {
       db.panelDao().replaceIns(panel)
   }

    suspend fun removePanel(panel: Panel) {
        db.panelDao().remove(panel)
    }

    fun getPanel(id: Int) {
        db.panelDao().getPanel(id)
    }
}