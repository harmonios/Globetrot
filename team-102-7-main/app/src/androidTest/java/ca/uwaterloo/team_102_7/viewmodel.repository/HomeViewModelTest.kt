package ca.uwaterloo.team_102_7.viewmodel.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uwaterloo.team_102_7.data.domain.User
import ca.uwaterloo.team_102_7.data.repository.ItineraryRepository
import ca.uwaterloo.team_102_7.data.repository.LocalStorageRepository
import ca.uwaterloo.team_102_7.data.repository.UserRepository
import ca.uwaterloo.team_102_7.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK

@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    private lateinit var context: Context
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @MockK
    private lateinit var userRepository: UserRepository
    @MockK
    private lateinit var itineraryRepository: ItineraryRepository
    @MockK
    private lateinit var localStorageRepository: LocalStorageRepository

    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)
        homeViewModel = HomeViewModel(userRepository, itineraryRepository, localStorageRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getUser_updatesSavedUserIdAndUser() = runTest {
        // Arrange
        val userId = 1
        val user = User(
            id = userId,
            first = "First",
            last = "Last",
            email = "test@user.com",
            theme = "",
            language = "",
            currency = ""
        )

        coEvery { localStorageRepository.getUserIDFlow() } returns MutableStateFlow(userId)
        coEvery { userRepository.getUser(userId) } returns user

        // Act
        homeViewModel.getUser()
        advanceUntilIdle()

        // Assert
        assertEquals(userId, homeViewModel.savedUserId.first())
        assertEquals(user, homeViewModel.user.first())
    }

    @Test
    fun retrieveCheckboxItems_updatesCheckBoxItems() = runTest {
        // Arrange
        val userId = 1
        val items = listOf("Item1", "Item2", "Item3")

        coEvery { localStorageRepository.getCheckboxItemsFlow(userId) } returns MutableStateFlow(items)

        // Act
        homeViewModel.retrieveCheckboxItems(userId)
        advanceUntilIdle()

        // Assert
        assertEquals(items, homeViewModel.checkBoxItems.first())
    }

    @Test
    fun saveCheckboxItems_savesItemsToRepository() = runTest {
        // Arrange
        val userId = 1
        val items = listOf("Item1", "Item2")

        // Act
        homeViewModel.saveCheckboxItems(userId, items)
        advanceUntilIdle()

        // Assert
        coVerify { localStorageRepository.saveCheckboxItems(userId, items) }
    }
}