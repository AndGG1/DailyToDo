package com.example.myapplication.MainLogic.UI.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.myapplication.MainLogic.Data.Model.Days
import com.example.myapplication.MainLogic.Data.Model.TaskItemBean
import com.example.myapplication.MainLogic.Data.Repository.PanelRepository
import com.example.myapplication.MainLogic.UI.MainWindowActivity
import com.example.myapplication.MainLogic.UI.ViewModels.PanelViewModel
import com.example.myapplication.R

class TaskSettingsFragment(contextOfApp: Context, panelId: String, listenerA: MainWindowActivity.AdapterListener, taskA: TaskItemBean, daysA: Days) : Fragment() {
    val copyOfContext = contextOfApp
    val pId = panelId
    val listener: MainWindowActivity.AdapterListener = listenerA
    val task: TaskItemBean = taskA
    val days = daysA

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_task_settings_panel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val composeView = view.findViewById<ComposeView>(R.id.taskSettingsCompose)
        val panelViewModel = PanelViewModel(PanelRepository(copyOfContext))
        composeView.setContent {
            TaskSettingsFragment_cmp(panelViewModel, pId, listener, task, days)
        }
    }
}