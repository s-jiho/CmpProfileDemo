package jp.jiho.cmpprofiledemo.ui.editprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpprofiledemo.composeapp.generated.resources.Res
import cmpprofiledemo.composeapp.generated.resources.edit_profile__back_content_description
import cmpprofiledemo.composeapp.generated.resources.edit_profile__field_bio
import cmpprofiledemo.composeapp.generated.resources.edit_profile__field_email
import cmpprofiledemo.composeapp.generated.resources.edit_profile__field_image_url
import cmpprofiledemo.composeapp.generated.resources.edit_profile__field_name
import cmpprofiledemo.composeapp.generated.resources.edit_profile__image_preview_content_description
import cmpprofiledemo.composeapp.generated.resources.edit_profile__notification_label
import cmpprofiledemo.composeapp.generated.resources.edit_profile__save_button
import cmpprofiledemo.composeapp.generated.resources.edit_profile__title
import coil3.compose.AsyncImage
import jp.jiho.cmpprofiledemo.presentation.editprofile.EditProfileViewModel
import jp.jiho.cmpprofiledemo.ui.common.toUserMessage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onSaveComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.savedEvent) {
        if (uiState.savedEvent) onSaveComplete()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.edit_profile__title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.edit_profile__back_content_description)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = uiState.profileImageUrl,
                contentDescription = stringResource(Res.string.edit_profile__image_preview_content_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
            )
            OutlinedTextField(
                value = uiState.profileImageUrl,
                onValueChange = viewModel::onImageUrlChange,
                label = { Text(stringResource(Res.string.edit_profile__field_image_url)) },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.fieldErrors.profileImageUrlError != null,
                supportingText = uiState.fieldErrors.profileImageUrlError?.let { error ->
                    {
                        Text(stringResource(error.errorMessage))
                    }
                },
            )
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(Res.string.edit_profile__field_name)) },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.fieldErrors.nameError != null,
                supportingText = uiState.fieldErrors.nameError?.let { error ->
                    {
                        Text(stringResource(error.errorMessage))
                    }
                },
            )
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(stringResource(Res.string.edit_profile__field_email)) },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.fieldErrors.emailError != null,
                supportingText = uiState.fieldErrors.emailError?.let { error ->
                    {
                        Text(stringResource(error.errorMessage))
                    }
                },
            )
            OutlinedTextField(
                value = uiState.bio,
                onValueChange = viewModel::onBioChange,
                label = { Text(stringResource(Res.string.edit_profile__field_bio)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = uiState.fieldErrors.bioError != null,
                supportingText = uiState.fieldErrors.bioError?.let { error ->
                    {
                        Text(stringResource(error.errorMessage))
                    }
                },
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(Res.string.edit_profile__notification_label),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = uiState.notificationEnabled,
                    onCheckedChange = viewModel::onNotificationToggle
                )
            }
            Button(
                onClick = viewModel::onSave,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(Res.string.edit_profile__save_button))
                }
            }
            uiState.saveError?.let {
                Text(stringResource(it.toUserMessage()))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
