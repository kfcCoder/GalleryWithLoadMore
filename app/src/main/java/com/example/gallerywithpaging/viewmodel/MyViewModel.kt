package com.example.pagergallerypaging.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.gallerywithpaging.TAG
import com.example.pagergallerypaging.api.RetrofitInstance
import com.example.pagergallerypaging.model.Hit
import com.example.pagergallerypaging.repository.Repository
import com.example.pagergallerypaging.util.Constants.Companion.DATA_STATUS_CAN_LOAD_MORE
import com.example.pagergallerypaging.util.Constants.Companion.DATA_STATUS_NETWORK_ERROR
import com.example.pagergallerypaging.util.Constants.Companion.DATA_STATUS_NO_MORE
import kotlinx.coroutines.launch
import kotlin.math.ceil

/**
 * pass in Repository to construct
 */
class MyViewModel(private val repository: Repository) : ViewModel() {
    /**
     * 加載圖片的LiveData
     */
    private val _photoListLive = MutableLiveData<List<Hit>>()
    val photoListLive: LiveData<List<Hit>>
        get() = _photoListLive

    /**
     * 加載狀況的LiveData
     */
    private val _dataStatusLive = MutableLiveData<Int>()
    val dataStatusLive: LiveData<Int>
        get() = _dataStatusLive
    
    private fun getQuery() = arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")
        .random()

    private val perPage = 200
    private var currentPage = 1
    private var totalPage = 1
    private var currentKey = "cat"
    private var isNewQuery = true
    private var isLoading = false

    init {
        resetQuery()
    }

    fun resetQuery() {
        currentPage = 1
        totalPage = 1
        currentKey = getQuery()
        isNewQuery = true
        fetchData()
    }

    /**
     * update LiveData from Pixabay server by Retrofit
     */
    fun fetchData(){
        if (isLoading) return // 加載中

        if (currentPage > totalPage) { // 所有內容已加載完畢
            _dataStatusLive.value = DATA_STATUS_NO_MORE
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPixabayResponse(query = currentKey,
                    page = currentPage, perPage = perPage)
                totalPage = ceil(response.body()?.totalHits?.toDouble()!! / perPage).toInt()
                if (response.isSuccessful) { // 成功取得response
                    if (isNewQuery) {
                        _photoListLive.value = response.body()?.hits // List<Hit>
                    } else {
                        _photoListLive.value = listOf(_photoListLive.value!!, response.body()?.hits!!).flatten()
                    }
                    _dataStatusLive.value = DATA_STATUS_CAN_LOAD_MORE
                    isLoading = false
                    isNewQuery = false
                    currentPage++
                } else {
                    Log.e(TAG, "Fetch data error: ${response.message()}")
                }
            } catch (e: Exception) { // handle 飛航模式
                _dataStatusLive.value = DATA_STATUS_NETWORK_ERROR
                isLoading = false
                Log.e(TAG, "Fetch data error: ${e.message}")
            }
        }
    }



    /**-------------Debug------------------------------------------------------------------------
     * OK!!
     */
    private val _testListLive = MutableLiveData<List<Hit>>()

    val testListLive: LiveData<List<Hit>>
        get() = _testListLive

    fun fetchTestData(){
        viewModelScope.launch {
            val response = repository.testPixabayResponse()
            if (response.isSuccessful) {
                _testListLive.value = response.body()?.hits
            } else {
                Log.e(TAG, "Fetch test data error: ${response.message()}")
            }
        }
    }

}