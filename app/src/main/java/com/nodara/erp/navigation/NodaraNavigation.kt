package com.nodara.erp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nodara.erp.NodaraApp
import com.nodara.erp.data.api.RetrofitClient
import com.nodara.erp.data.repository.*
import com.nodara.erp.presentation.activity.ActivityScreen
import com.nodara.erp.presentation.activity.ActivityViewModel
import com.nodara.erp.presentation.auth.AuthViewModel
import com.nodara.erp.presentation.auth.LoginScreen
import com.nodara.erp.presentation.auth.RegisterScreen
import com.nodara.erp.presentation.components.NodaraTopBar
import com.nodara.erp.presentation.contacts.ContactsScreen
import com.nodara.erp.presentation.contacts.ContactsViewModel
import com.nodara.erp.presentation.dashboard.DashboardScreen
import com.nodara.erp.presentation.dashboard.DashboardViewModel
import com.nodara.erp.presentation.finance.FinanceScreen
import com.nodara.erp.presentation.finance.FinanceViewModel
import com.nodara.erp.presentation.inventory.InventoryScreen
import com.nodara.erp.presentation.inventory.InventoryViewModel
import com.nodara.erp.presentation.profile.ProfileScreen
import com.nodara.erp.presentation.profile.ProfileViewModel
import com.nodara.erp.presentation.projects.ProjectsScreen
import com.nodara.erp.presentation.projects.ProjectsViewModel
import com.nodara.erp.presentation.purchases.PurchasesScreen
import com.nodara.erp.presentation.purchases.PurchasesViewModel
import com.nodara.erp.presentation.reports.ReportsScreen
import com.nodara.erp.presentation.reports.ReportsViewModel
import com.nodara.erp.presentation.sales.SalesScreen
import com.nodara.erp.presentation.sales.SalesViewModel
import com.nodara.erp.presentation.team.TeamScreen
import com.nodara.erp.presentation.team.TeamViewModel
import com.nodara.erp.presentation.theme.*
import kotlinx.coroutines.launch

@Composable
fun NodaraNavigation(
    navController: NavHostController = rememberNavController()
) {
    val sessionManager = remember { NodaraApp.instance.sessionManager }
    val isLoggedIn = remember { sessionManager.isLoggedIn() }

    val apiService = remember {
        RetrofitClient.getApiService(sessionManager, onUnauthorized = {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        })
    }

    val authRepo = remember { AuthRepository(apiService, sessionManager) }
    val dashboardRepo = remember { DashboardRepository(apiService) }
    val inventoryRepo = remember { InventoryRepository(apiService) }
    val salesRepo = remember { SalesRepository(apiService) }
    val contactsRepo = remember { ContactsRepository(apiService) }
    val purchasesRepo = remember { PurchasesRepository(apiService) }
    val financeRepo = remember { FinanceRepository(apiService) }
    val projectsRepo = remember { ProjectsRepository(apiService) }
    val teamRepo = remember { TeamRepository(apiService) }
    val activityRepo = remember { ActivityRepository(apiService) }
    val reportsRepo = remember { ReportsRepository(apiService) }

    val startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isAuthRoute = currentRoute == Screen.Login.route || currentRoute == Screen.Register.route

    if (isAuthRoute) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(Screen.Login.route) {
                val vm = remember { AuthViewModel(authRepo) }
                LoginScreen(
                    viewModel = vm,
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            composable(Screen.Register.route) {
                val vm = remember { AuthViewModel(authRepo) }
                RegisterScreen(
                    viewModel = vm,
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = NodaraInk,
                    drawerContentColor = NodaraMist
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(NodaraSignature, shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("N", color = NodaraPaper, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Nodara ERP", color = NodaraPaper, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Cliente Móvil", color = NodaraMist.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = NodaraLine.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Screen.drawerScreens.forEach { screen ->
                        NavigationDrawerItem(
                            label = { Text(screen.title, fontWeight = FontWeight.Medium) },
                            icon = if (screen.icon != null) { { Icon(screen.icon, contentDescription = null) } } else null,
                            selected = currentRoute == screen.route,
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Dashboard.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = NodaraSignature,
                                selectedIconColor = NodaraPaper,
                                selectedTextColor = NodaraPaper,
                                unselectedContainerColor = NodaraInk,
                                unselectedIconColor = NodaraMist.copy(alpha = 0.7f),
                                unselectedTextColor = NodaraMist.copy(alpha = 0.9f)
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider(color = NodaraLine.copy(alpha = 0.2f))
                    NavigationDrawerItem(
                        label = { Text("Cerrar Sesión", color = NodaraDanger) },
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = NodaraDanger) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            authRepo.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    val activeScreen = Screen.drawerScreens.find { it.route == currentRoute }
                    NodaraTopBar(
                        title = activeScreen?.title ?: "Nodara ERP",
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable(Screen.Dashboard.route) {
                            val vm = remember { DashboardViewModel(dashboardRepo) }
                            DashboardScreen(
                                viewModel = vm,
                                onNavigateToModule = { route ->
                                    navController.navigate(route) {
                                        popUpTo(Screen.Dashboard.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                        composable(Screen.Inventory.route) {
                            val vm = remember { InventoryViewModel(inventoryRepo) }
                            InventoryScreen(viewModel = vm)
                        }
                        composable(Screen.Sales.route) {
                            val vm = remember { SalesViewModel(salesRepo, inventoryRepo, contactsRepo) }
                            SalesScreen(viewModel = vm)
                        }
                        composable(Screen.Contacts.route) {
                            val vm = remember { ContactsViewModel(contactsRepo) }
                            ContactsScreen(viewModel = vm)
                        }
                        composable(Screen.Purchases.route) {
                            val vm = remember { PurchasesViewModel(purchasesRepo, inventoryRepo, contactsRepo) }
                            PurchasesScreen(viewModel = vm)
                        }
                        composable(Screen.Finance.route) {
                            val vm = remember { FinanceViewModel(financeRepo) }
                            FinanceScreen(viewModel = vm)
                        }
                        composable(Screen.Projects.route) {
                            val vm = remember { ProjectsViewModel(projectsRepo) }
                            ProjectsScreen(viewModel = vm)
                        }
                        composable(Screen.Team.route) {
                            val vm = remember { TeamViewModel(teamRepo) }
                            TeamScreen(viewModel = vm)
                        }
                        composable(Screen.Activity.route) {
                            val vm = remember { ActivityViewModel(activityRepo) }
                            ActivityScreen(viewModel = vm)
                        }
                        composable(Screen.Reports.route) {
                            val vm = remember { ReportsViewModel(reportsRepo) }
                            ReportsScreen(viewModel = vm)
                        }
                        composable(Screen.Profile.route) {
                            val vm = remember { ProfileViewModel(authRepo, reportsRepo) }
                            ProfileScreen(
                                viewModel = vm,
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
