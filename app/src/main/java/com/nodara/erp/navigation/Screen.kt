package com.nodara.erp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Login : Screen("login", "Iniciar Sesión")
    object Register : Screen("register", "Registrar Empresa")

    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Inventory : Screen("inventory", "Inventario", Icons.Default.Inventory2)
    object Sales : Screen("sales", "Ventas", Icons.Default.PointOfSale)
    object Contacts : Screen("contacts", "Contactos", Icons.Default.People)
    object Purchases : Screen("purchases", "Compras", Icons.Default.ShoppingCart)
    object Finance : Screen("finance", "Finanzas", Icons.Default.AccountBalanceWallet)
    object Projects : Screen("projects", "Proyectos", Icons.Default.Assignment)
    object Team : Screen("team", "Equipo", Icons.Default.Groups)
    object Activity : Screen("activity", "Actividad", Icons.Default.History)
    object Reports : Screen("reports", "Exportaciones", Icons.Default.PictureAsPdf)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)

    companion object {
        val drawerScreens = listOf(
            Dashboard,
            Inventory,
            Sales,
            Contacts,
            Purchases,
            Finance,
            Projects,
            Team,
            Activity,
            Reports,
            Profile
        )
    }
}
