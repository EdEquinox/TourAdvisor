package pt.isec.touradvisor.ui.screens

enum class Screens(val display: String, val showAppBar: Boolean) {
    LANDING("Landing",false),
    LOGIN("Login",true),
    HOME("Home",true),
    SETTINGS("Settings",true),
    PROFILE("Profile",true);

    val route: String
        get() = this.toString()
}