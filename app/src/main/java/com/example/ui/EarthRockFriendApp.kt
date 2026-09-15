package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.gemini.GeminiClient
import com.example.data.local.AppDatabase
import com.example.data.repository.MineralRepository
import com.example.ui.navigation.AppDrawer
import com.example.ui.navigation.Screen
import com.example.ui.screens.camera.CameraScreen
import com.example.ui.screens.camera.CameraViewModel
import com.example.ui.screens.chat.GemConsultScreen
import com.example.ui.screens.chat.GemConsultViewModel
import com.example.ui.screens.favorites.FavoritesScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.library.LoreLibraryScreen
import com.example.ui.screens.library.SpecimenDetailScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.search.SearchViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarthRockFriendApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Initialize Database, Repository, and Gemini Client
    val database = remember { AppDatabase.getDatabase(context, coroutineScope) }
    val repository = remember { MineralRepository(database.mineralDao()) }
    val geminiClient = remember { GeminiClient() }

    LaunchedEffect(Unit) {
        repository.ensureDatabasePopulated()
    }

    val allMinerals by repository.allMinerals.collectAsStateWithLifecycle(initialValue = emptyList())
    val favoriteMinerals by repository.favoriteMinerals.collectAsStateWithLifecycle(initialValue = emptyList())

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory(repository))
    val cameraViewModel: CameraViewModel = viewModel(factory = CameraViewModel.Factory(repository, geminiClient))
    val gemConsultViewModel: GemConsultViewModel = viewModel(factory = GemConsultViewModel.Factory(geminiClient))

    var initialGemConsultPrompt by remember { mutableStateOf<String?>(null) }

    // Featured mineral for HomeScreen
    val featuredMineral = remember(allMinerals) {
        allMinerals.firstOrNull { it.name == "Malachite" } ?: allMinerals.firstOrNull()
    }

    val topBarTitle = when {
        currentRoute.startsWith("detail") -> "Specimen Dossier"
        currentRoute == Screen.Home.route -> "Earth Rock Friend"
        currentRoute == Screen.Search.route -> "Physical Search"
        currentRoute == Screen.CameraScan.route -> "Rock Scanner"
        currentRoute == Screen.LoreLibrary.route -> "Lore & Safety"
        currentRoute == Screen.GemConsult.route -> "GemConsult AI"
        currentRoute == Screen.Favorites.route -> "My Field Collection"
        else -> "Earth Rock Friend"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                closeDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                // Don't show top app bar on detail screen as detail screen has its own back button top bar
                if (!currentRoute.startsWith("detail")) {
                    TopAppBar(
                        title = {
                            Text(
                                text = topBarTitle,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { coroutineScope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("top_bar_hamburger_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open Drawer"
                                )
                            }
                        },
                        actions = {
                            if (currentRoute != Screen.Search.route) {
                                IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                            }
                            if (currentRoute != Screen.CameraScan.route) {
                                IconButton(onClick = { navController.navigate(Screen.CameraScan.route) }) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Scanner")
                                }
                            }
                            if (currentRoute != Screen.Favorites.route) {
                                IconButton(onClick = { navController.navigate(Screen.Favorites.route) }) {
                                    Icon(Icons.Default.Star, contentDescription = "Favorites")
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Home Screen
                composable(Screen.Home.route) {
                    HomeScreen(
                        featuredMineral = featuredMineral,
                        onNavigate = { screen ->
                            navController.navigate(screen.route)
                        },
                        onSelectMineral = { id ->
                            navController.navigate(Screen.Detail.createRoute(id))
                        }
                    )
                }

                // Physical Search Screen
                composable(Screen.Search.route) {
                    SearchScreen(
                        viewModel = searchViewModel,
                        onSelectMineral = { id ->
                            navController.navigate(Screen.Detail.createRoute(id))
                        }
                    )
                }

                // Multi-Modal Camera Screen
                composable(Screen.CameraScan.route) {
                    CameraScreen(
                        viewModel = cameraViewModel,
                        onNavigate = { screen ->
                            navController.navigate(screen.route)
                        }
                    )
                }

                // Lore & Safety Screen
                composable(Screen.LoreLibrary.route) {
                    LoreLibraryScreen(
                        minerals = allMinerals,
                        onSelectMineral = { id ->
                            navController.navigate(Screen.Detail.createRoute(id))
                        }
                    )
                }

                // GemConsult AI Screen
                composable(Screen.GemConsult.route) {
                    GemConsultScreen(
                        viewModel = gemConsultViewModel,
                        initialPrompt = initialGemConsultPrompt
                    )
                }

                // Favorites Screen
                composable(Screen.Favorites.route) {
                    FavoritesScreen(
                        favoriteMinerals = favoriteMinerals,
                        onSelectMineral = { id ->
                            navController.navigate(Screen.Detail.createRoute(id))
                        },
                        onToggleFavorite = { id, currentFav ->
                            coroutineScope.launch {
                                repository.setFavorite(id, !currentFav)
                            }
                        }
                    )
                }

                // Specimen Detail Screen
                composable(
                    route = Screen.Detail.route,
                    arguments = listOf(navArgument("specimenId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val specimenId = backStackEntry.arguments?.getInt("specimenId") ?: 0
                    val mineral = allMinerals.firstOrNull { it.id == specimenId }

                    SpecimenDetailScreen(
                        mineral = mineral,
                        onBack = { navController.popBackStack() },
                        onToggleFavorite = { id, isFav ->
                            coroutineScope.launch {
                                repository.setFavorite(id, !isFav)
                            }
                        },
                        onSaveNotes = { id, notes ->
                            coroutineScope.launch {
                                repository.updateNotes(id, notes)
                            }
                        },
                        onConsultAi = { prompt ->
                            initialGemConsultPrompt = prompt
                            navController.navigate(Screen.GemConsult.route)
                        }
                    )
                }
            }
        }
    }
}
