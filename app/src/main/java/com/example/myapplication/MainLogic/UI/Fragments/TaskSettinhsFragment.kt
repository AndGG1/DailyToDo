package com.example.myapplication.MainLogic.UI.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.myapplication.MainLogic.Data.Repository.PanelRepository
import com.example.myapplication.MainLogic.UI.ViewModels.PanelViewModel
import com.example.myapplication.R

class TaskSettingsFragment(contextOfApp: Context) : Fragment() {
    val copyOfContext = contextOfApp

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
            TaskSettingsFragment_cmp(panelViewModel)
        }
    }
}