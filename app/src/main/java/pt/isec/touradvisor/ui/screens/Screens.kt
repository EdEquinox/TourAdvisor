package pt.isec.touradvisor.ui.screens

enum class Screens(val display: String, val showAppBar: Boolean) {
    LANDING("Landing",false),
    LOGIN("Login",false),
    HOME("Home",false),
    SETTINGS("Settings",true),
    PROFILE("Profile",true),
    SEARCH("Search",true);

    val route: String
        get() = this.toString()
}