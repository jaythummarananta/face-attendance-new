//package com.ananta.faceapp.viewModel
//
//import android.content.Context
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.ananta.faceapp.ApiRepo.AuthApi
//import com.ananta.faceapp.data.employeeModel.Item
//import kotlinx.coroutines.launch
//
//class AuthViewModel(context: Context) : ViewModel() {
//    private val authApi = AuthApi.getInstance(context)
//    private val _users = MutableLiveData<List<Item>>(emptyList())
//    val users: LiveData<List<Item>> get() = _users
//
//    private val _isLoading = MutableLiveData(false)
//    val isLoading: LiveData<Boolean> get() = _isLoading
//
//    fun fetchAllUserData() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            val response = authApi.getAllUserAccounts()
//            if (response != null && response.success) {
//                _users.value = response.data?.items ?: emptyList()
//            }
//            _isLoading.value = false
//        }
//    }
//}
package com.ananta.faceapp.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ananta.faceapp.ApiRepo.AuthApi
import com.ananta.faceapp.data.employeeModel.Item
import kotlinx.coroutines.launch
import java.io.IOException

class AuthViewModel(context: Context) : ViewModel() {
    private val authApi = AuthApi.getInstance(context)


    private val _users = MutableLiveData<List<Item>>(emptyList())
    val users: LiveData<List<Item>> get() = _users
    // LiveData for authentication state
    private val _isAuthenticated = MutableLiveData(false)
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    fun isAuthenticated( boolean: Boolean) {
        _isAuthenticated.value = boolean
    }
        // Implement your authentication logic here
        // Return true if authenticated, false otherwise
    fun fetchAllUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = authApi.getAllUserAccounts()
                Log.d("","response.data ${response}")
                if (response != null && response.success) {


                    _users.value = response.data?.items ?: emptyList()
                } else {
                    _error.value = "Failed to fetch users: ${response?.data ?: "Unknown error"}"
                }
            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
            
                val response = authApi.doDeleteUser(userId)
                if (response == true) {
                    // Refresh the list after deletion
                    fetchAllUserData()
                } else {
                    _error.value = "Failed to delete user: ${"Unknown error"}"
                }
            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}