package ca.uwaterloo.team_102_7.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.navigation.NavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uwaterloo.team_102_7.data.domain.User
import ca.uwaterloo.team_102_7.data.repository.AuthRepository
import ca.uwaterloo.team_102_7.data.repository.LocalStorageRepository
import ca.uwaterloo.team_102_7.ui.viewmodel.LoginViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.auth.exception.AuthRestException
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private val testDispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var authRepository: AuthRepository

    @MockK
    private lateinit var localStorageRepository: LocalStorageRepository

    @MockK
    private lateinit var navController: NavController

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)

        // Mock Toast.makeText to prevent Looper errors
        mockkStatic(Toast::class)
        every { Toast.makeText(any(), any<String>(), any()) } returns mockk(relaxed = true)

        loginViewModel = LoginViewModel(authRepository, localStorageRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Toast::class)
    }

    @Test
    fun googleSignIn_successful_navigatesToHome() = runTest {
        // Arrange
        val mockRequest = mockk<GetCredentialRequest>()
        val mockIdToken = "header.${encodeBase64("{'given_name':'John','family_name':'Doe','email':'john@example.com'}")}.signature"
        val userId = 123

        // Mock all necessary calls
        coEvery { authRepository.initGoogleSSO(any()) } returns mockRequest
        coEvery { authRepository.verifyUser(context, mockRequest, any()) } returns mockIdToken
        coEvery { localStorageRepository.saveUserUUID(any()) } just runs

        // Mock User companion object completely
        mockkObject(User.Companion)
        coEvery {
            User.appendToDB(
                first = "John",
                last = "Doe",
                email = "john@example.com"
            )
        } returns userId

        coEvery { navController.navigate(any<String>()) } just runs

        // Be specific about which Toast.makeText overload we're mocking
        val mockToast = mockk<Toast>()
        every {
            Toast.makeText(
                eq(context),
                eq("Successfully signed with Google!"),
                eq(Toast.LENGTH_LONG)
            )
        } returns mockToast
        every { mockToast.show() } just runs

        // Act
        loginViewModel.googleSignIn(context, navController)

        // Assert
        coVerifySequence {
            authRepository.initGoogleSSO(any())
            authRepository.verifyUser(context, mockRequest, any())
            User.appendToDB("John", "Doe", "john@example.com")
            localStorageRepository.saveUserUUID(userId)
            Toast.makeText(context, "Successfully signed with Google!", Toast.LENGTH_LONG)
            mockToast.show()
            navController.navigate("home")
        }

        // Clean up
        unmockkObject(User.Companion)
    }

    @Test
    fun googleSignIn_noCredentialException_showsError() = runTest {
        // Arrange
        val mockRequest = mockk<GetCredentialRequest>()
        coEvery { authRepository.initGoogleSSO(any()) } returns mockRequest
        coEvery { authRepository.verifyUser(context, mockRequest, any()) } throws NoCredentialException("No credential")

        // Act
        loginViewModel.googleSignIn(context, navController)

        // Assert
        coVerify {
            authRepository.initGoogleSSO(any())
            authRepository.verifyUser(context, mockRequest, any())
        }
        verify { Toast.makeText(context, "Please sign into Google on your device!", Toast.LENGTH_LONG) }
        coVerify(exactly = 0) { navController.navigate(any<String>()) }
    }

    @Test
    fun googleSignIn_getCredentialException_showsError() = runTest {
        // Arrange
        val mockRequest = mockk<GetCredentialRequest>()
        val mockException = mockk<GetCredentialException>()
        coEvery { authRepository.initGoogleSSO(any()) } returns mockRequest
        coEvery { authRepository.verifyUser(context, mockRequest, any()) } throws mockException

        // Act
        loginViewModel.googleSignIn(context, navController)

        // Assert
        coVerify {
            authRepository.initGoogleSSO(any())
            authRepository.verifyUser(context, mockRequest, any())
        }
        verify { Toast.makeText(context, "Please sign into Google on your device!", Toast.LENGTH_LONG) }
        coVerify(exactly = 0) { navController.navigate(any<String>()) }
    }

    @Test
    fun googleSignIn_tokenParsingException_showsError() = runTest {
        // Arrange
        val mockRequest = mockk<GetCredentialRequest>()
        val mockThrowable = mockk<Throwable>()
        coEvery { authRepository.initGoogleSSO(any()) } returns mockRequest
        coEvery { authRepository.verifyUser(context, mockRequest, any()) } throws GoogleIdTokenParsingException(mockThrowable)

        // Act
        loginViewModel.googleSignIn(context, navController)

        // Assert
        coVerify {
            authRepository.initGoogleSSO(any())
            authRepository.verifyUser(context, mockRequest, any())
        }
        verify { Toast.makeText(context, "Failed to parse id_token!", Toast.LENGTH_LONG) }
        coVerify(exactly = 0) { navController.navigate(any<String>()) }
    }

    @Test
    fun googleSignIn_authRestException_showsError() = runTest {
        // Arrange
        val mockRequest = mockk<GetCredentialRequest>()
        coEvery { authRepository.initGoogleSSO(any()) } returns mockRequest
        coEvery { authRepository.verifyUser(context, mockRequest, any()) } throws AuthRestException(
            message = "Auth error",
            statusCode = 401,
            errorCode = "auth/invalid-credentials"
        )

        // Act
        loginViewModel.googleSignIn(context, navController)

        // Assert
        coVerify {
            authRepository.initGoogleSSO(any())
            authRepository.verifyUser(context, mockRequest, any())
        }
        verify { Toast.makeText(context, "Authentication failed! Auth error", Toast.LENGTH_LONG) }
        coVerify(exactly = 0) { navController.navigate(any<String>()) }
    }

    private fun encodeBase64(input: String): String {
        return android.util.Base64.encodeToString(
            input.toByteArray(),
            android.util.Base64.NO_WRAP or android.util.Base64.URL_SAFE
        )
    }
}