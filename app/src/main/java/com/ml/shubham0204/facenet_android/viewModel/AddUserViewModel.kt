package com.ml.shubham0204.facenet_android.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddUserViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Form field states
    var firstName = mutableStateOf(savedStateHandle.get<String>("firstName") ?: "")
        set(value) {
            field = value
            savedStateHandle["firstName"] = value.value
        }
    var lastName = mutableStateOf(savedStateHandle.get<String>("lastName") ?: "")
        set(value) {
            field = value
            savedStateHandle["lastName"] = value.value
        }
    var email = mutableStateOf(savedStateHandle.get<String>("email") ?: "")
        set(value) {
            field = value
            savedStateHandle["email"] = value.value
        }
    var phone = mutableStateOf(savedStateHandle.get<String>("phone") ?: "")
        set(value) {
            field = value
            savedStateHandle["phone"] = value.value
        }
    var birthDate = mutableStateOf(savedStateHandle.get<String>("birthDate") ?: "")
        set(value) {
            field = value
            savedStateHandle["birthDate"] = value.value
        }
    var bloodGroup = mutableStateOf(savedStateHandle.get<String>("bloodGroup") ?: "")
        set(value) {
            field = value
            savedStateHandle["bloodGroup"] = value.value
        }
    var userRole = mutableStateOf(savedStateHandle.get<String>("userRole") ?: "")
        set(value) {
            field = value
            savedStateHandle["userRole"] = value.value
        }
    var department = mutableStateOf(savedStateHandle.get<String>("department") ?: "")
        set(value) {
            field = value
            savedStateHandle["department"] = value.value
        }

    // Image paths state
    val imagePaths = mutableStateListOf<String?>(
        savedStateHandle.get<String>("imagePath1"),
        savedStateHandle.get<String>("imagePath2"),
        savedStateHandle.get<String>("imagePath3"),
        savedStateHandle.get<String>("imagePath4")
    )

    // Update image path at specific index
    fun updateImagePath(index: Int, path: String?) {
        imagePaths[index] = path
        savedStateHandle["imagePath${index + 1}"] = path
    }

    // Clear state (optional, if needed for reset)
    fun clear() {
        firstName.value = ""
        lastName.value = ""
        email.value = ""
        phone.value = ""
        birthDate.value = ""
        bloodGroup.value = ""
        userRole.value = ""
        department.value = ""
        imagePaths.clear()
        imagePaths.addAll(listOf(null, null, null, null))
        savedStateHandle.remove<String>("firstName")
        savedStateHandle.remove<String>("lastName")
        savedStateHandle.remove<String>("email")
        savedStateHandle.remove<String>("phone")
        savedStateHandle.remove<String>("birthDate")
        savedStateHandle.remove<String>("bloodGroup")
        savedStateHandle.remove<String>("userRole")
        savedStateHandle.remove<String>("department")
        savedStateHandle.remove<String>("imagePath1")
        savedStateHandle.remove<String>("imagePath2")
        savedStateHandle.remove<String>("imagePath3")
        savedStateHandle.remove<String>("imagePath4")
    }
}