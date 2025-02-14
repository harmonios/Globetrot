package ca.uwaterloo.team_102_7.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uwaterloo.team_102_7.data.domain.User
import ca.uwaterloo.team_102_7.data.repository.LocalStorageRepository
import ca.uwaterloo.team_102_7.data.repository.UserRepository
import ca.uwaterloo.team_102_7.ui.viewmodel.SettingsViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var localStorageRepository: LocalStorageRepository

    private lateinit var settingsViewModel: SettingsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)
        settingsViewModel = SettingsViewModel(userRepository, localStorageRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getUser_successful_updatesUserState() = runTest {
        // Arrange
        val testUser = User(
            id = 1,
            first = "John",
            last = "Doe",
            email = "john@example.com",
            theme = "Classic",
            language = "English",
            currency = "CAD"
        )

        coEvery { localStorageRepository.getUserIDFlow() } returns flowOf(1)
        coEvery { userRepository.getUser(1) } returns testUser

        // Act
        settingsViewModel.getUser()

        // Assert
        assertEquals(1, settingsViewModel.savedUserId.value)
        assertEquals(testUser, settingsViewModel.user.value)
        assertEquals("John", settingsViewModel.selectedFName.value)
        assertEquals("Doe", settingsViewModel.selectedlName.value)
        assertEquals("john@example.com", settingsViewModel.selectedEmail.value)
        assertEquals("English", settingsViewModel.selectedLanguage.value)
        assertEquals("Classic", settingsViewModel.selectedTheme.value)
        assertEquals("CAD", settingsViewModel.selectedCurrency.value)
    }

    @Test
    fun updateFName_successful_updatesRepository() = runTest {
        // Arrange
        val testUser = User(
            id = 1,
            first = "John",
            last = "Doe",
            email = "john@example.com",
            theme = "Classic",
            language = "English",
            currency = "CAD"
        )

        coEvery { localStorageRepository.getUserIDFlow() } returns flowOf(1)
        coEvery { userRepository.getUser(1) } returns testUser

        // Set up initial state
        settingsViewModel.getUser()

        // Act
        val newName = "Jane"
        settingsViewModel.updateFName(newName)

        // Assert
        coVerify { userRepository.updateFName(1, newName) }
        assertEquals(newName, settingsViewModel.selectedFName.value)
    }

    @Test
    fun updateLName_successful_updatesRepository() = runTest {
        // Arrange
        val testUser = User(
            id = 1,
            first = "John",
            last = "Doe",
            email = "john@example.com",
            theme = "Classic",
            language = "English",
            currency = "CAD"
        )

        coEvery { localStorageRepository.getUserIDFlow() } returns flowOf(1)
        coEvery { userRepository.getUser(1) } returns testUser

        // Set up initial state
        settingsViewModel.getUser()

        // Act
        val newName = "Smith"
        settingsViewModel.updateLName(newName)

        // Assert
        coVerify { userRepository.updateLName(1, newName) }
        assertEquals(newName, settingsViewModel.selectedlName.value)
    }

    @Test
    fun updateTheme_successful_updatesRepository() = runTest {
        // Arrange
        val testUser = User(
            id = 1,
            first = "John",
            last = "Doe",
            email = "john@example.com",
            theme = "Classic",
            language = "English",
            currency = "CAD"
        )

        coEvery { localStorageRepository.getUserIDFlow() } returns flowOf(1)
        coEvery { userRepository.getUser(1) } returns testUser

        // Set up initial state
        settingsViewModel.getUser()

        // Act
        val newTheme = "Dark"
        settingsViewModel.updateTheme(newTheme)

        // Assert
        coVerify { userRepository.updateTheme(1, newTheme) }
        assertEquals(newTheme, settingsViewModel.selectedTheme.value)
    }

    @Test
    fun updateLanguage_successful_updatesRepository() = runTest {
        // Arrange
        val testUser = User(
            id = 1,
            first = "John",
            last = "Doe",
            email = "john@example.com",
            theme = "Classic",
            language = "English",
            currency = "CAD"
        )

        coEvery { localStorageRepository.getUserIDFlow() } returns flowOf(1)
        coEvery { userRepository.getUser(1) } returns testUser

        // Set up initial state
        settingsViewModel.getUser()

        // Act
        val newLanguage = "French"
        settingsViewModel.updateLanguage(newLanguage)

        // Assert
        coVerify { userRepository.updateLanguage(1, newLanguage) }
        assertEquals(newLanguage, settingsViewModel.selectedLanguage.value)
    }

    @Test
    fun updateCurrency_successful_updatesRepository() = runTest {
        // Arrange
        val testUser = User(
            id = 1,
            first = "John",
            last = "Doe",
            email = "john@example.com",
            theme = "Classic",
            language = "English",
            currency = "CAD"
        )

        coEvery { localStorageRepository.getUserIDFlow() } returns flowOf(1)
        coEvery { userRepository.getUser(1) } returns testUser

        // Set up initial state
        settingsViewModel.getUser()

        // Act
        val newCurrency = "USD"
        settingsViewModel.updateCurrency(newCurrency)

        // Assert
        coVerify { userRepository.updateCurrency(1, newCurrency) }
        assertEquals(newCurrency, settingsViewModel.selectedCurrency.value)
    }

    @Test
    fun convertCurrency_CADtoUSD_returnsCorrectAmount() {
        // Arrange
        val amount = 100.0

        // Act
        val result = settingsViewModel.convertCurrency("CAD", "USD", amount)

        // Assert
        assertEquals(72.0, result, 0.01)
    }

    @Test
    fun convertCurrency_USDtoCAD_returnsCorrectAmount() {
        // Arrange
        val amount = 100.0

        // Act
        val result = settingsViewModel.convertCurrency("USD", "CAD", amount)

        // Assert
        assertEquals(139.0, result, 0.01)
    }

    @Test
    fun convertCurrency_sameCurrentcy_returnsOriginalAmount() {
        // Arrange
        val amount = 100.0

        // Act
        val result = settingsViewModel.convertCurrency("CAD", "CAD", amount)

        // Assert
        assertEquals(amount, result, 0.01)
    }
}