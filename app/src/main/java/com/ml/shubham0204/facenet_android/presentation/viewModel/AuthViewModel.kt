package com.ananta.faceapp.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ananta.faceapp.ApiRepo.AuthApi
import com.ananta.faceapp.data.employeeModel.Item
import kotlinx.coroutines.launch

class AuthViewModel(context: Context) : ViewModel() {
    private val authApi = AuthApi.getInstance(context)
    private val _users = MutableLiveData<List<Item>>(emptyList())
    val users: LiveData<List<Item>> get() = _users

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchAllUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = authApi.getAllUserAccounts()
            if (response != null && response.success) {
                _users.value = response.data?.items ?: emptyList()
            }
            _isLoading.value = false
        }
    }
}