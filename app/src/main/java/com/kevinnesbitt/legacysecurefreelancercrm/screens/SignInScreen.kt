package com.kevinnesbitt.legacysecurefreelancercrm.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.R
import com.kevinnesbitt.legacysecurefreelancercrm.util.DialogBox
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(viewModel: HomeViewModel, navController: NavController, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val windowInfo = LocalWindowInfo.current.containerDpSize
    // val screenHeight = windowInfo.height
    val screenWidth = windowInfo.width

    // val diagonalGradient = Brush.linearGradient(
    //     colors = listOf(
    //         Color.White,
    //         Color(0xFFE0E0E0) // Light gray
    //     ),
    //     start = Offset(0f, Float.POSITIVE_INFINITY), // Bottom-Left
    //     end = Offset(Float.POSITIVE_INFINITY, 0f) // Top-Right
    // )

    val scrollState = rememberScrollState()

    var emailText by remember { mutableStateOf("") }
    var fullNameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var confirmPasswordText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showSignInError by remember { mutableStateOf(false) }
    var showRegisterError by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var showAccountOverwrite by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.signin_background_nologo),
            contentDescription = "Background",
            Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        AnimatedContent(
            targetState = isRegistering,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
            },
            label = "Auth Resize Fade"
        ) { registering ->

            if (registering) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                        .padding(bottom = 100.dp)
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.legacy_logo_three),
                        contentDescription = "Logo",
                        modifier = Modifier.size(150.dp)
                    )

                    Text(
                        text = "Lets Get You Started",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 15.dp, bottom = 40.dp)
                    )

                    Card(
                        elevation = CardDefaults.cardElevation(if (showRegisterError) 5.dp else 0.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.sizeIn(
                            maxWidth = screenWidth - 70.dp,
                            minWidth = screenWidth - 70.dp,
                            minHeight = 50.dp,
                            maxHeight = 100.dp
                        ),
                        colors = CardDefaults.cardColors(containerColor = if (showRegisterError) Color(0xFFFFEBEEL) else Color.Transparent, contentColor = if (showRegisterError) Color(0xFFC62828L) else Color.Transparent)
                    ) {
                        if (showRegisterError) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFC62828L),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Text(
                    //     text = "Full Name",
                    //     fontSize = 17.sp,
                    //     fontWeight = FontWeight.Bold,
                    //     color = Color.DarkGray,
                    //     modifier = Modifier
                    //         .padding(top = 20.dp)
                    //         .width(screenWidth - 90.dp),
                    //     textAlign = TextAlign.Start
                    // )

                    OutlinedTextField(
                        value = fullNameText,
                        onValueChange = { text ->
                            if (fullNameText.length <= 35) {
                                fullNameText = text
                            }
                        },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(screenWidth - 90.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )

                    // Text(
                    //     text = "Email",
                    //     fontSize = 17.sp,
                    //     fontWeight = FontWeight.Bold,
                    //     color = Color.DarkGray,
                    //     modifier = Modifier
                    //         .padding(top = 20.dp)
                    //         .width(screenWidth - 90.dp),
                    //     textAlign = TextAlign.Start
                    // )

                    OutlinedTextField(
                        value = emailText,
                        onValueChange = { text ->
                            if (emailText.length <= 35) {
                                emailText = text
                            }
                        },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(screenWidth - 90.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )

                    // Text(
                    //     text = "Password",
                    //     fontSize = 17.sp,
                    //     fontWeight = FontWeight.Bold,
                    //     color = Color.DarkGray,
                    //     modifier = Modifier
                    //         .padding(top = 20.dp)
                    //         .width(screenWidth - 90.dp),
                    //     textAlign = TextAlign.Start
                    // )

                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { text ->
                            if (text.length <= 35) {
                                passwordText = text
                            }
                        },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .width(screenWidth - 90.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        shape = RoundedCornerShape(18.dp)
                    )

                    // Text(
                    //     text = "Confirm Password",
                    //     fontSize = 17.sp,
                    //     fontWeight = FontWeight.Bold,
                    //     color = Color.DarkGray,
                    //     modifier = Modifier
                    //         .padding(top = 20.dp)
                    //         .width(screenWidth - 90.dp),
                    //     textAlign = TextAlign.Start
                    // )

                    OutlinedTextField(
                        value = confirmPasswordText,
                        onValueChange = { text ->
                            if (text.length <= 35) {
                                confirmPasswordText = text
                            }
                        },
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 25.dp)
                            .width(screenWidth - 90.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    confirmPasswordVisible = !confirmPasswordVisible
                                },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        shape = RoundedCornerShape(18.dp)
                    )

                    Button(
                        onClick = {
                            showPrivacyPolicy = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        modifier = Modifier
                            .size(width = screenWidth - 90.dp, height = 55.dp)
                    ) {
                        Text(
                            text = "Create",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp, bottom = 30.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Have an account?",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(end = 7.dp)
                        )

                        Text(
                            text = "Log In",
                            fontSize = 13.sp,
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable(
                                onClick = {
                                    isRegistering = false
                                    showRegisterError = false
                                }
                            )
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.legacy_logo_three),
                        contentDescription = "Logo",
                        modifier = Modifier.size(150.dp)
                    )

                    Text(
                        text = "Welcome Back",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 15.dp, bottom = 40.dp)
                    )

                    Card(
                        elevation = CardDefaults.cardElevation(if (showSignInError) 5.dp else 0.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.sizeIn(
                            maxWidth = screenWidth - 70.dp,
                            minWidth = screenWidth - 70.dp,
                            minHeight = 50.dp,
                            maxHeight = 100.dp
                        ),
                        colors = CardDefaults.cardColors(containerColor = if (showSignInError) Color(0xFFFFEBEEL) else Color.Transparent, contentColor = if (showRegisterError) Color(0xFFC62828L) else Color.Transparent)
                    ) {
                        if (showSignInError) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFC62828L),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Text(
                    //     text = "Email",
                    //     fontSize = 17.sp,
                    //     fontWeight = FontWeight.Bold,
                    //     color = Color.DarkGray,
                    //     modifier = Modifier
                    //         .padding(top = 20.dp)
                    //         .width(screenWidth - 90.dp),
                    //     textAlign = TextAlign.Start
                    // )

                    OutlinedTextField(
                        value = emailText,
                        onValueChange = { text ->
                            if (emailText.length <= 35) {
                                emailText = text
                            }
                        },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(screenWidth - 90.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )

                    // Text(
                    //     text = "Password",
                    //     fontSize = 17.sp,
                    //     fontWeight = FontWeight.Bold,
                    //     color = Color.DarkGray,
                    //     modifier = Modifier
                    //         .padding(top = 20.dp)
                    //         .width(screenWidth - 90.dp),
                    //     textAlign = TextAlign.Start
                    // )

                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { text ->
                            if (text.length <= 35) {
                                passwordText = text
                            }
                        },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 25.dp)
                            .width(screenWidth - 90.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                isProcessing = true
                                showSignInError = false
                                viewModel.signInUser(emailText, passwordText) { success, message ->
                                    if (success) {
                                        viewModel.restoreFromCloud { restoreSuccess, restoreMessage ->
                                            if (restoreSuccess) {
                                                ""
                                            }
                                        }
                                    } else {
                                        errorMessage = message?: "Unknown Error occurred. Please contact us or try again later."
                                        showSignInError = true
                                    }
                                }

                                isProcessing = false
                            }
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        shape = RoundedCornerShape(18.dp)
                    )

                    Button(
                        onClick = {
                            isProcessing = true
                            showSignInError = false
                            viewModel.signInUser(emailText, passwordText) { success, message ->
                                if (success) {
                                    viewModel.restoreFromCloud { restoreSuccess, restoreMessage ->
                                        if (restoreSuccess) {
                                            ""
                                        }
                                    }
                                } else {
                                    errorMessage = message?: "Unknown Error occurred. Please contact us or try again later."
                                    showSignInError = true
                                }
                            }

                            isProcessing = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        modifier = Modifier
                            .size(width = screenWidth - 90.dp, height = 55.dp)
                    ) {
                        Text(
                            text = "Login",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp, bottom = 30.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account?",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(end = 7.dp)
                        )

                        Text(
                            text = "Create an account",
                            fontSize = 13.sp,
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable(
                                onClick = {
                                    showSignInError = false
                                    isRegistering = true
                                }
                            )
                        )
                    }

                    Card(
                        elevation = CardDefaults.cardElevation(5.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        showAccountOverwrite = true
                                    }
                                )
                                .background(color = Color.White)
                                .padding(15.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.google_logo),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(25.dp)
                            )

                            Text(
                                text = "Sign in with Google",
                                color = Color.Black,
                                fontSize = 17.sp,
                                modifier = Modifier.padding(start = 20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if(isProcessing) {
        Dialog(
            onDismissRequest = {  }
        ) {
            CircularProgressIndicator(color = Color.Cyan)
        }
    }

    if (showAccountOverwrite) {
        DialogBox(
            onDismissRequest = { showAccountOverwrite = false },
            title = "Confirm Google Sign In?",
            iconImageVector = Icons.Default.Warning,
            description = "Signing in with Google will delete and overwrite any existing email in our records that are the same as your Gmail along with your data\n\nAre you sure you want to continue?",
            height = 350.dp,
            buttonRow = {
                Button(
                    onClick = { showAccountOverwrite = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 15.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val credentialManager = CredentialManager.create(context)

                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId("184878633293-ega8at47m794rai5u49o1eq2a2tmknq6.apps.googleusercontent.com")
                                    .setAutoSelectEnabled(true)
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                val result = credentialManager.getCredential(context, request)
                                val credential = result.credential

                                if (credential is CustomCredential &&
                                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                    val idToken = googleIdTokenCredential.idToken

                                    // Pass the verified Google token and the extracted Google email
                                    viewModel.signInWithGoogle(
                                        idToken = idToken, // <-- Safe, non-empty email from Google
                                        onSuccess = {
                                            viewModel.restoreFromCloud { restoreSuccess, restoreMessage ->

                                                // 2. Navigate to home regardless of whether restore found data or not
                                                navController.navigate("home") {
                                                    popUpTo("auth") { inclusive = true }
                                                    launchSingleTop = true
                                                }
                                            }
                                        },
                                        onError = { message ->
                                            errorMessage = message
                                            showSignInError = true
                                        }
                                    )
                                }
                            } catch (e: Exception) {
                                if (e !is androidx.credentials.exceptions.GetCredentialCancellationException) {
                                    Toast.makeText(context, "Sign-in failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        showAccountOverwrite = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text(
                        text = "Confirm",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        ) {  }
    }

    if (showPrivacyPolicy) {
        Dialog(
            onDismissRequest = { showPrivacyPolicy = false }
        ) {
            Surface(
                color = Color.White,
                modifier = Modifier.size(350.dp, 550.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(7.dp)
                        .background(color = Color.White),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Privacy Policy",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(7.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "By accepting, you certify that you have read and acknowledged our policy on collecting and handling user data.",
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    HorizontalDivider(thickness = 2.dp, color = Color.Black)

                    LazyColumn(
                        modifier = Modifier
                            .size(width = 350.dp, height = 350.dp)
                            .background(color = Color.White)
                    ) {
                        item {
                            Text(
                                text = """
                                    Privacy Policy for Legacy: Secure Freelancer CRM
                                    Last Updated: July 2026
                                    
                                    Thank you for choosing Legacy: Secure Freelancer CRM! Your privacy and the security of your business data are of the utmost importance to us. This Privacy Policy explains how our app handles your information.
                                    
                                    1. Information Collection and Storage
                                    Legacy is designed to give you flexible access to your professional data while ensuring transparency:
                                    
                                    Data We Collect: The app stores the information you input, which may include your own name and contact details, your clients' names and contact details, and invoice information.
                                    
                                    Where Data is Stored: Your data is stored locally on your device as well as synchronized with Firebase as a third-party cloud database. This allows your records to be securely saved both on your device and through cloud infrastructure to support your workflow.
                                    
                                    2. Third-Party Services and Payments
                                    We utilize trusted third-party cloud and billing services to support app functionality:
                                    
                                    Firebase: We use Firebase as a third-party database service to securely handle data storage and synchronization alongside local device storage.
                                    
                                    Google Play Billing: If you purchase a monthly subscription to use the app, the payment transaction is handled entirely and securely by Google Play Billing. Legacy never accesses, processes, or stores your financial information, credit card numbers, or billing addresses.
                                    
                                    Analytics & Advertising: We do not use any third-party tracking, analytics, or advertising tools.
                                    
                                    3. Data Control and Deletion
                                    You maintain control over the records and information you input into the app:
                                    
                                    Data Deletion: You can delete individual records, client details, invoices, or all input data at any time directly through the app interface.
                                    
                                    Account and Data Removal: If you wish to fully remove your data from both your device and our cloud database (Firebase), you can manage or delete your information within the app settings or by reaching out to us directly.
                                    
                                    Uninstalling the App: Uninstalling Legacy from your device will remove the local app files, but cloud-synced records may remain in Firebase unless explicitly deleted through the app prior to uninstalling.
                                    
                                    4. Children’s Privacy
                                    Legacy: Secure Freelancer CRM is intended for professional use by individuals of all ages and has no specific age restrictions. We do not knowingly collect personal information from children under the age of 13.
                                    
                                    5. Changes to This Privacy Policy
                                    We may update this Privacy Policy from time to time. Any revisions will be reflected by the "Last Updated" date at the top of this document. We encourage you to review this policy periodically.
                                    
                                    6. Contact Us
                                    If you have any questions, concerns, or feedback regarding this Privacy Policy, please contact us at:
                                    Email: kevnes522@gmail.com
                                """.trimIndent(),
                                color = Color.Black,
                                fontSize = 13.sp
                            )
                        }
                    }

                    HorizontalDivider(thickness = 2.dp, color = Color.Black)

                    Button(
                        onClick = {
                            viewModel.acceptPrivacyPolicy(true)

                            isProcessing = true
                            showRegisterError = false
                            if (passwordText == confirmPasswordText) {
                                viewModel.updateUserName(fullNameText, emailText)

                                viewModel.signUpUser(emailText, passwordText) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        ""
                                    } else {
                                        errorMessage = message?: "Unknown error occurred. Please contact us or try again later."
                                        showRegisterError = true
                                    }
                                }
                            } else if (fullNameText.isEmpty()) {
                                showRegisterError = true
                                errorMessage = "Please enter your name."
                            } else {
                                showRegisterError = true
                                errorMessage = "Passwords do not match."
                            }

                            isProcessing = false
                            showPrivacyPolicy = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                    ) {
                        Text(
                            text = "Accept",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(7.dp),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}