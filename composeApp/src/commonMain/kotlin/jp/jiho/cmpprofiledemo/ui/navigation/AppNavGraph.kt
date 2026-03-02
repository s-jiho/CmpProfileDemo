package jp.jiho.cmpprofiledemo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import jp.jiho.cmpprofiledemo.ui.editprofile.EditProfileScreen
import jp.jiho.cmpprofiledemo.ui.profile.ProfileScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ProfileRoute) {
        composable<ProfileRoute> {
            ProfileScreen(
                onEditClick = { navController.navigate(EditProfileRoute) }
            )
        }
        composable<EditProfileRoute> {
            EditProfileScreen(
                onSaveComplete = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
