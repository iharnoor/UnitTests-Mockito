package com.example.wallpaperapp

import com.example.wallpaperapp.Utils.Resource
import com.example.wallpaperapp.data.api.PicSumApi
import com.example.wallpaperapp.data.api.WallpaperRepostiryImpl
import com.example.wallpaperapp.data.api.model.PicSumItem
import com.example.wallpaperapp.domain.entity.WallpaperLink
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlin.math.exp

class DataRepositoryTest {

    private lateinit var mockApiService: PicSumApi

    @Mock
    private lateinit var employeeMock: Employee

    private lateinit var dataRepository: WallpaperRepostiryImpl

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mockApiService = mock(PicSumApi::class.java)
        dataRepository = WallpaperRepostiryImpl(mockApiService)
    }


    @Test
    fun testGetImagesCase1Success() = runBlocking {
        // Case 1 network call is successful
        // then response will have some date
        // val response = picSumApi.getWallpaperImages()

        val mockPicSumItem = mock(PicSumItem::class.java)
        val mockListPicSumItem = listOf(mockPicSumItem)


//        `when`(mockApiService.getWallpaperImages()).thenReturn(mockListPicSumItem)
        `when`(mockApiService.getWallpaperImages()).thenReturn(mockListPicSumItem)
        val response = dataRepository.getImages().toList()

        // check if the response is corect with the data
        // List<WallpaperLink> == mockListPicSumItem
        val mockWallpaperList: List<WallpaperLink>? = mockListPicSumItem?.map {
            WallpaperLink(it.downloadUrl.orEmpty())
        }
        assertEquals(response.size, mockListPicSumItem.size) // Ensure the lists have the same size

        for (i in 0 until response.size) {
            val expectedResource = Resource.Success(mockWallpaperList)
            val actualResource = response[i] as Resource.Success<List<WallpaperLink>>
            assertEquals(expectedResource.javaClass, actualResource.javaClass)
            // Manually compare the contents of the lists
            assertEquals(expectedResource.data, actualResource.data)
        }
    }

    @Test
    fun testGetImagesFailed() = runBlocking {
        val errorMessage = "Test message"
        `when`(mockApiService.getWallpaperImages()).thenAnswer { throw Exception(errorMessage) }

        // Actual
        val result = dataRepository.getImages().toList()

        // Assert
        val expectedErrorResource = Resource.Error(null, errorMessage)

        assertEquals(expectedErrorResource.javaClass, result[0].javaClass)

        if (expectedErrorResource is Resource.Error && result[0] is Resource.Error) {
            assertEquals(expectedErrorResource.message, (result[0] as Resource.Error).message)
        } else {
            fail("Expected and actual are not Resource.Error instances")
        }
    }
}

class Employee(val name: String, val id: Int) {

}