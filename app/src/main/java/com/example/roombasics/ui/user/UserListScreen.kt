package com.example.roombasics.ui.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roombasics.data.database.AppDatabase
import com.example.roombasics.data.database.entities.User
import com.example.roombasics.data.repository.UserRepository
import com.example.roombasics.ui.theme.RoomBasicsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            UserRepository(AppDatabase.getDatabase(LocalContext.current).userDao())
        )
    )
) {
    val users by userViewModel.userList.collectAsStateWithLifecycle()

    val firstNameInput by userViewModel.firstNameInput.collectAsStateWithLifecycle()
    val lastNameInput by userViewModel.lastNameInput.collectAsStateWithLifecycle()
    val emailInput by userViewModel.emailInput.collectAsStateWithLifecycle()
    val editingUser by userViewModel.editingUser.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("CRUD de Usuarios con Room y Compose") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserInputForm(
                firstName = firstNameInput,
                lastName = lastNameInput,
                email = emailInput,
                onFirstNameChange = userViewModel::onFirstNameChange,
                onLastNameChange = userViewModel::onLastNameChange,
                onEmailChange = userViewModel::onEmailChange,
                onSaveUser = userViewModel::saveUser,
                isEditing = editingUser != null,
                onCancelEdit = userViewModel::cancelEditing
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Lista de Usuarios:", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(users, key = { it.id }) { user ->
                    UserCard(
                        user = user,
                        onDeleteClick = { userViewModel.deleteUser(it) },
                        onEditClick = { userViewModel.startEditingUser(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun UserInputForm(
    firstName: String,
    lastName: String,
    email: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onSaveUser: () -> Unit,
    isEditing: Boolean,
    onCancelEdit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = onSaveUser,
                modifier = Modifier.weight(1f),
                enabled = firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()
            ) {
                Text(if (isEditing) "Guardar Cambios" else "Añadir Usuario")
            }
            if (isEditing) {
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onCancelEdit, //
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onEditClick: (User) -> Unit,
    onDeleteClick: (User) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ID: ${user.id}", style = MaterialTheme.typography.bodySmall)
                Text("${user.firstName} ${user.lastName}", style = MaterialTheme.typography.titleMedium)
                Text(user.email, style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                IconButton(onClick = { onEditClick(user) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { onDeleteClick(user) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserListScreen() {
    RoomBasicsTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                UserInputForm(
                    firstName = "Juan",
                    lastName = "Pérez",
                    email = "juan@example.com",
                    onFirstNameChange = {},
                    onLastNameChange = {},
                    onEmailChange = {},
                    onSaveUser = {},
                    isEditing = false,
                    onCancelEdit = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Lista de Usuarios (Preview):", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                UserCard(
                    user = User(1, "Ana", "Gómez", "ana@example.com"),
                    onEditClick = {},
                    onDeleteClick = {}
                )
                UserCard(
                    user = User(2, "Pedro", "García", "pedro@example.com"),
                    onEditClick = {},
                    onDeleteClick = {}
                )
            }
        }
    }
}