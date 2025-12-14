package Database.NotificationDB

sealed class PanelState {
    object LOADING: PanelState()
    object SUCCESS: PanelState()
}
