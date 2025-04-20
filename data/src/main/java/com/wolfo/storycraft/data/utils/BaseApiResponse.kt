package com.wolfo.storycraft.data.utils

import retrofit2.Response

abstract class BaseApiResponse {

    suspend fun <T> safeApiCall(api: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = api()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                } ?: return errorMessage("Body is empty")
            }
            else {
                return errorMessage("${response.code()} ${response.message()}")
            }

        } catch (e: Exception) {
            return errorMessage<T>(e.message.toString())
        }
    }

    private fun <T> errorMessage(e: String): NetworkResult.Error<T> =
        NetworkResult.Error(data = null, message = "Api call failed ${e}")
}