package com.watsidev.kanbamboard.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun InputKanban(
    text: String,
    onTextChange: (String) -> Unit,
    label: String = "",
    leadingIcon: ImageVector,
    isPassword: Boolean,
    modifier: Modifier = Modifier
) {

    var passwordVisibility by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        label = {
            Text(text = label)
        },
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
        ),
        shape = RoundedCornerShape(100),
        leadingIcon = {
            Icon(
                leadingIcon,
                contentDescription = "Icono de entrada",
            )
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
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
        ),
        modifier = modifier
    )
}