package com.logisticspro.app.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.logisticspro.app.ui.theme.*
import com.logisticspro.app.ui.viewmodel.LoginState
import com.logisticspro.app.ui.viewmodel.LoginViewModel

private fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    onError("Hata: $errString")
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Doğrulama başarısız. Tekrar deneyin.")
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Logistics Pro")
        .setSubtitle("Parmak izinizle giriş yapın")
        .setNegativeButtonText("İptal")
        .build()

    biometricPrompt.authenticate(promptInfo)
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                isLoading = false
                onNavigateToHome()
            }
            is LoginState.Error -> {
                isLoading = false
                android.widget.Toast.makeText(context, (loginState as LoginState.Error).message, android.widget.Toast.LENGTH_SHORT).show()
            }
            is LoginState.Idle -> {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
    ) {
        // Background Decorative Elements
        BackgroundDecorations()

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Section
            BrandSection()

            Spacer(modifier = Modifier.height(48.dp))

            // Login Card
            LoginCard(
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                isLoading = isLoading,
                onLoginClick = { 
                    isLoading = true
                    viewModel.login(username, password)
                },
                onBiometricClick = {
                    val activity = context as? FragmentActivity
                    if (activity != null) {
                        showBiometricPrompt(
                            activity = activity,
                            onSuccess = { onNavigateToHome() },
                            onError = { err -> 
                                android.widget.Toast.makeText(context, err, android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        android.widget.Toast.makeText(context, "Biyometrik giriş desteklenmiyor", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Footer Meta
            FooterMeta()
        }
    }
}

@Composable
private fun BoxScope.BackgroundDecorations() {
    // Top-left blur
    Box(
        modifier = Modifier
            .size(400.dp)
            .offset((-100).dp, (-100).dp)
            .background(
                color = PrimaryContainer.copy(alpha = 0.05f),
                shape = RoundedCornerShape(50)
            )
            .blur(120.dp)
    )

    // Bottom-right blur
    Box(
        modifier = Modifier
            .size(300.dp)
            .align(Alignment.BottomEnd)
            .offset(50.dp, 50.dp)
            .background(
                color = SecondaryContainer.copy(alpha = 0.1f),
                shape = RoundedCornerShape(50)
            )
            .blur(100.dp)
    )
}

@Composable
private fun BrandSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo Box
        val rotation by animateFloatAsState(targetValue = 12f, label = "logoRotation")

        Box(
            modifier = Modifier
                .size(80.dp)
                .rotate(rotation)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Primary, PrimaryContainer)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = OnPrimary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Brand Name
        Text(
            text = "Logistics Pro",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Primary
        )

        // Subtitle
        Text(
            text = "Kinetic Intelligence Dashboard",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = OnSurfaceVariant,
            modifier = Modifier.customAlpha(0.7f)
        )
    }
}

@Composable
private fun LoginCard(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onBiometricClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp)
        ) {
            // Header
            Text(
                text = "Giriş Yap",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Operasyonel verilere erişmek için kimlik bilgilerinizi kullanın.",
                fontSize = 14.sp,
                color = OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Username Field
            UsernameField(
                value = username,
                onValueChange = onUsernameChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            PasswordField(
                value = password,
                onValueChange = onPasswordChange,
                isVisible = passwordVisible,
                onVisibilityToggle = onPasswordVisibilityToggle
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button
            LoginButton(
                isLoading = isLoading,
                onClick = onLoginClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Divider
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = OutlineVariant.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Biometric Section
            BiometricSection(onBiometricClick = onBiometricClick)
        }
    }
}

@Composable
private fun UsernameField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = "KULLANICI ADI",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceVariant,
            letterSpacing = 0.1.sp,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("alex.miller") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerHighest,
                unfocusedContainerColor = SurfaceContainerHighest,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = OnSurface,
                unfocusedTextColor = OnSurface,
                cursorColor = Secondary
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ŞİFRE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant,
                letterSpacing = 0.1.sp
            )

            TextButton(onClick = { /* TODO: Handle forgot password */ }) {
                Text(
                    text = "Şifremi Unuttum",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("••••••••") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerHighest,
                unfocusedContainerColor = SurfaceContainerHighest,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = OnSurface,
                unfocusedTextColor = OnSurface,
                cursorColor = Secondary
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp),
                strokeWidth = 2.dp,
                color = OnPrimary
            )
        }

        Text(
            text = "Giriş Yap",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = OnPrimary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            imageVector = Icons.Outlined.ArrowForward,
            contentDescription = null,
            tint = OnPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun BiometricSection(onBiometricClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "VEYA ŞUNUNLA DEVAM ET",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceVariant,
            letterSpacing = 0.1.sp,
            modifier = Modifier.customAlpha(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        IconButton(
            onClick = onBiometricClick,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = SurfaceContainer,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun FooterMeta() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // System Status
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "System Status",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = OnSurfaceVariant,
                    letterSpacing = 0.1.sp,
                    modifier = Modifier.customAlpha(0.4f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                color = StatusGreen,
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "WH-042 Online",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant
                    )
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(
                        color = OutlineVariant.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {}

            // Version
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Version",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = OnSurfaceVariant,
                    letterSpacing = 0.1.sp,
                    modifier = Modifier.customAlpha(0.4f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "v4.2.1-stable",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Güvenli lojistik ağı. Erişiminiz izlenmekte ve günlüklenmektedir.",
            fontSize = 11.sp,
            color = OnSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth(0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

private fun Modifier.customAlpha(alpha: Float) = this.then(
    Modifier.alpha(alpha)
)

// =============== PREVIEW ===============

@Preview(
    name = "Login Screen - Light Mode",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun LoginScreenPreviewLight() {
    LogisticsProTheme(darkTheme = false) {
        LoginScreen()
    }
}

@Preview(
    name = "Login Screen - Dark Mode",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun LoginScreenPreviewDark() {
    LogisticsProTheme(darkTheme = true) {
        LoginScreen()
    }
}

@Preview(
    name = "Login Screen - Tablet",
    showBackground = true,
    device = "spec:width=600dp,height=1024dp",
    showSystemUi = true
)
@Composable
fun LoginScreenPreviewTablet() {
    LogisticsProTheme {
        LoginScreen()
    }
}

@Preview(
    name = "Brand Section",
    showBackground = true,
    backgroundColor = 0xFFF7F9FF
)
@Composable
fun BrandSectionPreview() {
    LogisticsProTheme {
        BrandSection()
    }
}

@Preview(
    name = "Login Card",
    showBackground = true,
    backgroundColor = 0xFFF7F9FF,
    showSystemUi = false
)
@Composable
fun LoginCardPreview() {
    LogisticsProTheme {
        LoginCard(
            username = "test.user",
            onUsernameChange = {},
            password = "••••••••",
            onPasswordChange = {},
            passwordVisible = false,
            onPasswordVisibilityToggle = {},
            isLoading = false,
            onLoginClick = {},
            onBiometricClick = {}
        )
    }
}

@Preview(
    name = "Footer Meta",
    showBackground = true,
    backgroundColor = 0xFFF7F9FF
)
@Composable
fun FooterMetaPreview() {
    LogisticsProTheme {
        FooterMeta()
    }
}

