package com.watsidev.kanbanboard.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun InputKanban(
    text: String,
    onTextChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector?,
    isPassword: Boolean = false,
    isEmail: Boolean = false,
    isNext: Boolean = false,
    isGo: Boolean = false,
    isSearch: Boolean = false,
    onImeAction: () -> Unit = {},
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var passwordVisibility by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        label = {
            Text(text = label)
        },
        isError = isError,
        supportingText = {
            if (isError && errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedIndicatorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            focusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
            unfocusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
        ),
        shape = RoundedCornerShape(100),
        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    it,
                    contentDescription = "Icono de entrada",
                )
            }
        },
        trailingIcon = {
            if (isPassword) {
                val visibilityIcon = if (passwordVisibility) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility
                val description = if (passwordVisibility) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            }
        },
        visualTransformation = if (isPassword && !passwordVisibility) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = if (isPassword) KeyboardType.Password else if (isEmail) KeyboardType.Email else KeyboardType.Text,
            imeAction = if (isNext) ImeAction.Next
                        else if (isGo) ImeAction.Go
                        else if (isSearch) ImeAction.Search
                        else ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
                    onNext = { if (isNext) onImeAction() },
                    onGo = { if (isGo) onImeAction() },
                    onSearch = { if (isSearch) onImeAction() },
                    onDone = { onImeAction() }
                ),
        modifier = modifier
    )
}