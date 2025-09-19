package com.example.roombasics.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.roombasics.data.database.entities.User
import com.example.roombasics.data.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userList = MutableStateFlow<List<User>>(emptyList())
    val userList: StateFlow<List<User>> = _userList.asStateFlow()

    private val _firstNameInput = MutableStateFlow("")
    val firstNameInput: StateFlow<String> = _firstNameInput.asStateFlow()

    private val _lastNameInput = MutableStateFlow("")
    val lastNameInput: StateFlow<String> = _lastNameInput.asStateFlow()

    private val _emailInput = MutableStateFlow("")
    val emailInput: StateFlow<String> = _emailInput.asStateFlow()

    private val _editingUser = MutableStateFlow<User?>(null)
    val editingUser: StateFlow<User?> = _editingUser.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getAllUsers().collectLatest { users ->
                _userList.value = users
            }
        }
    }

    fun onFirstNameChange(name: String) { _firstNameInput.value = name }
    fun onLastNameChange(name: String) { _lastNameInput.value = name }
    fun onEmailChange(email: String) { _emailInput.value = email }

    fun startEditingUser(user: User) {
        _firstNameInput.value = user.firstName
        _lastNameInput.value = user.lastName
        _emailInput.value = user.email
        _editingUser.value = user
    }

    fun saveUser() {
        if (firstNameInput.value.isNotBlank() && lastNameInput.value.isNotBlank() && emailInput.value.isNotBlank()) {
            viewModelScope.launch {
                val currentEditingUser = _editingUser.value
                if (currentEditingUser != null) {
                    val updatedUser = currentEditingUser.copy(
                        firstName = firstNameInput.value,
                        lastName = lastNameInput.value,
                        email = emailInput.value
                    )
                    userRepository.updateUser(updatedUser)
                    _editingUser.value = null
                } else {
                    val newUser = User(
                        firstName = firstNameInput.value,
                        lastName = lastNameInput.value,
                        email = emailInput.value
                    )
                    userRepository.insertUser(newUser)
                }
                clearInputFields()
            }
        } else {
            println("Todos los campos son obligatorios para guardar/añadir un usuario.")
        }
    }

    fun clearInputFields() {
        _firstNameInput.value = ""
        _lastNameInput.value = ""
        _emailInput.value = ""
    }

    fun cancelEditing() {
        _editingUser.value = null
        clearInputFields()
    }


    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
        }
    }
}