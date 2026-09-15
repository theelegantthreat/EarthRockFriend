package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Field Companion")
    object Search : Screen("search", "Physical Search")
    object CameraScan : Screen("camera_scan", "Rock Scanner")
    object LoreLibrary : Screen("lore_library", "Lore & Safety Library")
    object GemConsult : Screen("gem_consult", "GemConsult AI")
    object Favorites : Screen("favorites", "My Field Collection")
    object Detail : Screen("detail/{specimenId}", "Specimen Dossier") {
        fun createRoute(specimenId: Int) = "detail/$specimenId"
    }
}
