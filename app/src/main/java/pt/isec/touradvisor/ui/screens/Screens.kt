package pt.isec.touradvisor.ui.screens

enum class Screens(val display: String, val showAppBar: Boolean) {
    LANDING("Landing",false),
    LOGIN("Login",true),
    REGISTER("Register",true),
    HOME("Home",true),
    POI("POI",true),
    FAVORITES("Favorites",true),
    VISITED("Visited",true),
    SETTINGS("Settings",true),
    PROFILE("Profile",true);

    val route: String
        get() = this.toString()
}