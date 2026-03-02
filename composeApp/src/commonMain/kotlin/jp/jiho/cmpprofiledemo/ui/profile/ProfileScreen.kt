package jp.jiho.cmpprofiledemo.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpprofiledemo.composeapp.generated.resources.Res
import cmpprofiledemo.composeapp.generated.resources.profile__fab_edit_content_description
import cmpprofiledemo.composeapp.generated.resources.profile__image_content_description
import coil3.compose.AsyncImage
import jp.jiho.cmpprofiledemo.domain.model.Profile
import jp.jiho.cmpprofiledemo.presentation.profile.ProfileViewModel
import jp.jiho.cmpprofiledemo.ui.common.toUserMessage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    onEditClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onEditClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.profile__fab_edit_content_description)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> Text(
                    text = stringResource(uiState.error!!.toUserMessage())
                )
                uiState.profile != null -> ProfileContent(profile = uiState.profile!!)
                else -> {}
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: Profile,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = profile.profileImageUrl,
            contentDescription = stringResource(Res.string.profile__image_content_description),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
        )
        Text(
            text = profile.name,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = profile.email,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = profile.bio,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
