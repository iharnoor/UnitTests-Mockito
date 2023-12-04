package com.example.wallpaperapp.data.api

import com.example.wallpaperapp.Utils.Resource
import com.example.wallpaperapp.domain.entity.WallpaperLink
import com.example.wallpaperapp.domain.repository.WallpaperRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class WallpaperRepostiryImpl @Inject constructor(val picSumApi: PicSumApi) : WallpaperRepository {

    // todo to write tests for getImages function
    override fun getImages(): Flow<Resource<List<WallpaperLink>>> = flow {

        try {
            val response = picSumApi.getWallpaperImages() // unit test are run with internet
            // response = mockData

            response?.let {
                val wallpaperLinks: List<WallpaperLink> = response.map {
                    WallpaperLink(it.downloadUrl.orEmpty())
                }
                emit(Resource.Success(wallpaperLinks))
            }
        } catch (e: Exception) {
            var errorOutput = ""
            if (e.message != null) {
                errorOutput = e.message!!
            } else {
                errorOutput = "Unknown Error"
            }
            emit(Resource.Error(null, errorOutput))

        }
    }

}