package it.skills.itskills.bot.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

fun <T> safeApiCall(call: suspend() -> Response<T>): Flow<Resource<T>> = flow {
    emit(Resource.Loading())

    var remoteData: Response<T>? = null
    try {
        remoteData = call()
        println("safeApiCall ${remoteData.code()}")
        val data = remoteData.body()
        println("safeApiCall $data")
        if (data != null) {
            emit(Resource.Success(data))
        } else {
            emit(Resource.Error("Null data"))
        }
    } catch (e: HttpException) {
        emit(
            Resource.Error(
                message = remoteData?.message() ?: e.message ?: Constants.HttpExceptionError,
                data = null
            )
        )
    } catch (e: IOException) {
        emit(
            Resource.Error(
                message = remoteData?.message() ?: e.message ?: Constants.IOExceptionError,
                data = null
            )
        )
    }
}


