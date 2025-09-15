package com.mobile.dataregisteration


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.mobile.dataregisteration.apiModel.UserRegistrationRequest
import com.mobile.dataregisteration.ui.theme.DataRegisterationTheme
import com.mobile.dataregistration.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContent {

            DataRegisterationTheme(darkTheme = false) {
                // MainScreen()

                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost( ) {
    val viewModel: MainViewModel = hiltViewModel()
    val loginResponse by viewModel.loginResponseFlow.collectAsState(initial = null)
    MainScreen()

    val navController = rememberNavController()
    val context = LocalContext.current
    /*LaunchedEffect(loginResponse) {
        if (loginResponse?.status == true) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true } // remove login from back stack
            }
        }
    }*/
    NavHost(navController, startDestination =   if (loginResponse?.status == true) "home" else "login") {
        composable("login") { LoginScreen(navController) }
        composable("home") {
            PhotoFormScreen(
                navToLogin = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true } // remove login from back stack
                    }
                },
                onSubmit = {
                    Toast.makeText(context, "Clicked Submit button", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}



@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Welcome Home 🎉", style = MaterialTheme.typography.headlineMedium)
    }
}

@SuppressLint("HardwareIds", "CoroutineCreationDuringComposition")
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current

    val viewModel: MainViewModel = hiltViewModel()
    val scrollState = rememberScrollState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val deviceId = remember {
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    // Launch permission request when screen enters
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        // onLocationFetched(location.latitude, location.longitude)
                        viewModel.latlong = "${location.latitude},${location.longitude}"
                    } else {
                        // Toast.makeText(context, "Location is null", Toast.LENGTH_SHORT).show()
                        viewModel.latlong = ""
                    }
                }
            }
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Automatically request permission once when screen appears
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .padding(18.dp)
                .alpha(1.0f)
                .clip(
                    RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp,
                        bottomStart = 10.dp,
                        bottomEnd = 10.dp
                    )
                )
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight()

        ) {
            Column(
                modifier = Modifier
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mla_pic_logo), // your drawable
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 200.dp, height = 150.dp), // custom width and height
                    contentScale = ContentScale.Fit // scales image to fill the size
                )
                Text(
                    text = "Login",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(
                        color = Color.DarkGray
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Email TextField
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shake(emailError != null),
                    isError = emailError != null
                )

                if (emailError != null) {
                    Text(
                        text = emailError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Password TextField
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shake(passwordError != null),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Default.Visibility
                        else Icons.Default.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    isError = passwordError != null
                )



                if (passwordError != null) {
                    Text(
                        text = passwordError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (uiState) {
                    is LoginUiState.Idle -> {
                        GlassyButton(
                            text = "Login",
                            onClick = {
                                if (!isNetworkAvailable(context)) {
                                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT)
                                        .show()
                                    return@GlassyButton
                                }
                                var valid = true

                                 if (email.isBlank()) {
                                     emailError = "Email is required"
                                     valid = false
                                 } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                     emailError = "Invalid email"
                                    valid = false
                                }
                                 if (password.isBlank()) {
                                     passwordError = "Password is required"
                                     valid = false
                                 } else if (password.length < 6) {
                                     passwordError = "Password must be at least 6 characters"
                                     valid = false
                                 }

                                if (valid) {

                                    /* navController.navigate("home") {
                                         popUpTo("login") { inclusive = true }
                                     }*/
                                    viewModel.login(
                                        deviceId = deviceId,
                                        userName = email,
                                        password = password
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled =true
                        )
                    }

                    is LoginUiState.Loading -> {
                        CircularProgressIndicator()
                        Text("Logging in...")
                    }

                    is LoginUiState.Success -> {
                        val data = (uiState as LoginUiState.Success).data
                        Log.w("Success","Login success! Token: ${data.accesstoken}")
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.saveLoginResponse(context, data)
                        }
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }

                    is LoginUiState.Error -> {
                        val error = (uiState as LoginUiState.Error).message
                        Text("Error: $error", color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.login(
                                deviceId = deviceId,
                                userName = "ragulcse_p",
                                password = "Ragul@123"
                            )
                        }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }

}

/*@Composable
fun StylishButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = Colors
                )
            )
            .clickable(onClick = onClick,
            indication = LocalIndication.current,
            interactionSource = remember { MutableInteractionSource() }
        )
            .shadow(6.dp, RoundedCornerShape(24.dp))
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}*/

@Composable
fun GlassyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var pressed by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(
                if (enabled) Color.DarkGray else Color.Gray.copy(alpha = 0.5f) // dim background
            )
            .border(
                width = 1.dp,
                color = if (enabled) Color.DarkGray else Color.Gray,
                shape = RoundedCornerShape(30.dp)
            )
            .clickable(
                enabled = enabled, // ✅ disables click when false
                indication = rememberRipple(bounded = true, color = Color.White),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                pressed = true
                onClick()
            }
            .shadow(
                if (pressed && enabled) 12.dp else 6.dp,
                RoundedCornerShape(30.dp)
            )
            .padding(vertical = 14.dp, horizontal = 24.dp)
    ) {
        Text(
            text = text,
            color = if (enabled) Color.White else Color.LightGray, // ✅ dim text when disabled
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        )
    }
}



@SuppressLint("RememberReturnType")
@Composable
fun Modifier.shake(enabled: Boolean): Modifier {
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(enabled) {
        if (enabled) {
            offsetX.snapTo(0f)
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    -20f at 50
                    20f at 100
                    -15f at 150
                    15f at 200
                    -10f at 250
                    10f at 300
                    0f at 400
                }
            )
        }
    }

    return this.offset { IntOffset(offsetX.value.toInt(), 0) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTopBar(
    viewModel: MainViewModel, onLogout: () -> Unit
) {
    /*TopAppBar(
        title = { Text("Register Here...") },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color(0xFF423202), // background color
            titleContentColor = Color.White       // title text color
        ),
        navigationIcon = { null },
        actions = {
            IconButton(onClick = {
                viewModel.logout()
                onLogout() // Navigate to Login screen
            }) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout"
                )
            }
        }
    )*/

    SmallTopAppBar(
        title = { Text("Register Here...",
            modifier = Modifier.padding(start = 0.dp)) },
        navigationIcon = {}, // remove reserved space by passing empty
        actions = {
            IconButton(onClick =  {viewModel.logout()}) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color(0xFF423202),
            titleContentColor = Color.White
        )
    )
}


@Composable
fun CustomAppBar(
    title: String,
    backgroundColor: Color = Color(0xFF423202),
    onLogout: () -> Unit
) {
    Surface(
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), // standard app bar height
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp), // optional horizontal padding
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }
        }
    }
}




@SuppressLint("HardwareIds")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoFormScreen(
    navToLogin: () -> Unit,
    onSubmit: (List<ImageUploadMap?>) -> Unit = {},
) {
    val context = LocalContext.current
    var latLong = ""
    val deviceId = remember {
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
    val viewModel: MainViewModel = hiltViewModel()
    val userId by viewModel.userId.collectAsState()
    val accessToken by viewModel.accessToken.collectAsState()

    val districts = remember { loadLocationData(context) }

    val isUploading by viewModel.isUploading.collectAsState()
    // Gallery launchers
    val galleryLauncherAadharP1 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        it?.let { viewModel.aadharFrontPhoto = uriToBitMap(it,context) }
    }
    val galleryLauncherAadharP2 =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { viewModel.aadharBackPhoto = uriToBitMap(it,context) }
        }
    val galleryLauncherRationCardP1 =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { viewModel.rationCardFrontPhoto = uriToBitMap(it,context) }
        }
    val galleryLauncherRationCardP2 =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { viewModel.rationCardBackPhoto = uriToBitMap(it,context) }
        }
    val galleryLauncherVoterIdP1 =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { viewModel.voterIdFrontPhoto = uriToBitMap(it,context) }
        }
    val galleryLauncherVoterIdP2 =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { viewModel.voterIdBackPhoto = uriToBitMap(it,context) }
        }
    val phoneFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    // Launch permission request when screen enters
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                       // onLocationFetched(location.latitude, location.longitude)
                        viewModel.latlong = "${location.latitude},${location.longitude}"
                    } else {
                       // Toast.makeText(context, "Location is null", Toast.LENGTH_SHORT).show()
                        viewModel.latlong = ""
                    }
                }
            }
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Automatically request permission once when screen appears
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        /*topBar = {
            TopAppBar({ BasicTopBar(
                viewModel = viewModel, onLogout = navToLogin
            ) }
            )
        }*/
    ) { padding ->
        MainScreen()
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(5.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CustomAppBar(title = "Register Here...", onLogout = {
                viewModel.logout()
                navToLogin()
            })
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .alpha(1.0f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomStart = 10.dp,
                            bottomEnd = 10.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.background)
                    .wrapContentHeight()

            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Name ", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleMedium, color = Color.Black)
                    OutlinedTextField(
                        value = viewModel.name,
                        onValueChange = { viewModel.name = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { phoneFocusRequester.requestFocus() } // Move to next field
                        )
                    )
                    Text(text = "Mobile Number ", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleMedium, color = Color.Black)
                    OutlinedTextField(
                        value = viewModel.phone,
                        onValueChange = { newValue ->
                            // Allow only digits
                            if (newValue.all { it.isDigit() } && newValue.length <= 10) {
                                viewModel. phone = newValue
                                // Validate length
                                viewModel. phoneError = newValue.length != 10
                            }
                            if (newValue.length == 10) {
                                // Close keyboard when 10 digits entered
                                keyboardController?.hide()
                            }
                        },
                        singleLine = true,
                        isError = viewModel.phoneError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { /* You can hide keyboard or submit form */ }
                        ),
                        modifier = Modifier.fillMaxWidth().focusRequester(phoneFocusRequester)
                    )

                    if (viewModel.phoneError && viewModel.phone.isNotEmpty()) {
                        Text(
                            text = "Mobile number must be 10 digits",
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                        // District dropdown
                        AnimatedDropdownOld(
                            label = "District",
                            options = districts.map { it.districtName },
                            selectedOption = viewModel.selectedDistrict?.districtName,
                            onOptionSelected = { name ->
                                viewModel.selectedDistrict = districts.first { it.districtName == name }
                                viewModel. selectedUnion = null
                                viewModel. selectedPanchayat = null
                                viewModel. selectedVillage = null
                            },
                            onOptionSelectedPosition = {}
                        )

                        // Union dropdown
                    viewModel. selectedDistrict?.let { district ->
                            Spacer(modifier = Modifier.height(8.dp))
                            AnimatedDropdownOld(
                                label = "Union",
                                options = district.unionList.map { it.unionName },
                                selectedOption =viewModel. selectedUnion?.unionName,
                                onOptionSelected = { name ->
                                    viewModel. selectedUnion = district.unionList.first { it.unionName == name }
                                    viewModel. selectedPanchayat = null
                                    viewModel. selectedVillage = null
                                },
                                onOptionSelectedPosition = {}
                            )
                        }

                        // Panchayat dropdown
                    viewModel. selectedUnion?.let { union ->
                            Spacer(modifier = Modifier.height(8.dp))
                            AnimatedDropdownOld(
                                label = "Panchayat",
                                options = union.panchayatList.map { it.panchayatName },
                                selectedOption = viewModel.selectedPanchayat?.panchayatName,
                                onOptionSelected = { name ->
                                    viewModel. selectedPanchayat = union.panchayatList.first { it.panchayatName == name }
                                    viewModel.  selectedVillage = null
                                },
                                onOptionSelectedPosition = {}
                            )
                        }

                        // Village dropdown
                    viewModel. selectedPanchayat?.let { panchayat ->
                            Spacer(modifier = Modifier.height(8.dp))
                            AnimatedDropdownOld(
                                label = "Village",
                                options = panchayat.villageList.map { it.villageName },
                                selectedOption = viewModel.selectedVillage?.villageName,
                                onOptionSelected = { name ->
                                    viewModel. selectedVillage = panchayat.villageList.first { it.villageName == name }
                                },
                                onOptionSelectedPosition = {}
                            )
                        }

                    PhotoQuestionSection(
                        questionText = "Aadhar",
                        photo1Bitmap = viewModel.aadharFrontPhoto,
                        photo2Bitmap =viewModel. aadharBackPhoto,
                        onFrontPhotoGalleryClick = { galleryLauncherAadharP1.launch("image/*") },
                        onFrontPhotoCameraCaptured = { bmp -> viewModel.aadharFrontPhoto = bmp  },
                        onFrontPhotoRemove = { viewModel.aadharFrontPhoto = null },
                        onBackPhotoGalleryClick = { galleryLauncherAadharP2.launch("image/*") },
                        onBackPhotoCameraCaptured = { bmp -> viewModel.aadharBackPhoto = bmp },
                        onBackPhotoRemove = { viewModel.aadharBackPhoto = null }
                    )

                    PhotoQuestionSection(
                        questionText = "Ration Card",
                        photo1Bitmap = viewModel.rationCardFrontPhoto,
                        photo2Bitmap = viewModel.rationCardBackPhoto,
                        onFrontPhotoGalleryClick = { galleryLauncherRationCardP1.launch("image/*") },
                        onFrontPhotoCameraCaptured = { bmp -> viewModel.rationCardFrontPhoto = bmp  },
                        onFrontPhotoRemove = { viewModel.rationCardFrontPhoto = null },
                        onBackPhotoGalleryClick = { galleryLauncherRationCardP2.launch("image/*") },
                        onBackPhotoCameraCaptured = { bmp -> viewModel.rationCardBackPhoto = bmp  },
                        onBackPhotoRemove = { viewModel.rationCardBackPhoto = null }
                    )

                    PhotoQuestionSection(
                        questionText = "Voter ID",
                        photo1Bitmap = viewModel.voterIdFrontPhoto,
                        photo2Bitmap = viewModel.voterIdBackPhoto,
                        onFrontPhotoGalleryClick = { galleryLauncherVoterIdP1.launch("image/*") },
                        onFrontPhotoCameraCaptured = { bmp -> viewModel.voterIdFrontPhoto = bmp  },
                        onFrontPhotoRemove = { viewModel.voterIdFrontPhoto = null },
                        onBackPhotoGalleryClick = { galleryLauncherVoterIdP2.launch("image/*") },
                        onBackPhotoCameraCaptured = { bmp -> viewModel.voterIdBackPhoto = bmp  },
                        onBackPhotoRemove = { viewModel.voterIdBackPhoto = null }
                    )

                    Spacer(Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(16.dp))
                    // Submit button
                    GlassyButton(
                        text = "Submit",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = viewModel.isFormValid,
                        onClick = {
                            if (!isNetworkAvailable(context)) {
                                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                                return@GlassyButton
                            }
                            val uploadImageList = mutableListOf<ImageUploadMap>()
                            uploadImageList.add(ImageUploadMap(nameOfThePhoto = "acardFront-${viewModel.phone}",
                                bitmap = viewModel.aadharFrontPhoto!!))
                            uploadImageList.add(ImageUploadMap(nameOfThePhoto = "acardBack-${viewModel.phone}",
                                bitmap = viewModel.aadharBackPhoto!!))
                            uploadImageList.add(ImageUploadMap(nameOfThePhoto = "rcardFront-${viewModel.phone}",
                                bitmap = viewModel.rationCardFrontPhoto!!))
                            uploadImageList.add(ImageUploadMap(nameOfThePhoto = "rcardBack-${viewModel.phone}",
                                bitmap = viewModel.rationCardBackPhoto!!))
                            uploadImageList.add(ImageUploadMap(nameOfThePhoto = "vcardFront-${viewModel.phone}",
                                bitmap = viewModel.voterIdFrontPhoto!!))
                            uploadImageList.add(ImageUploadMap(nameOfThePhoto = "vcardBack-${viewModel.phone}",
                                bitmap = viewModel.voterIdBackPhoto!!))

                            val userRegisterRequest = UserRegistrationRequest(
                                userId = userId.toString(),
                                name = viewModel.name,
                                phno = viewModel.phone,
                                union = viewModel.selectedUnion?.unionName.toString(),
                                panchayat = viewModel.selectedPanchayat?.panchayatName.toString(),
                                villege = viewModel.selectedVillage?.villageName.toString(),
                                district = viewModel.selectedDistrict?.districtName.toString(),
                                aadharCardFrontLink = "",
                                aadharCardBackLink = "",
                                rationCardFrontLink = "",
                                rationCardBackLink = "",
                                voterCardFrontLink = "",
                                voterCardBackLink = "",
                                deviceId = deviceId,
                                latLongitude = viewModel.latlong,
                                deviceType = "Android",
                                accesstoken = accessToken!!
                            )

                            userId?.let { viewModel.uploadPhotos(it,uploadImageList,userRegisterRequest){
                                    success, message ->
                                        if (success){
                                            viewModel.successMessage = "Registration completed successfully!"
                                            viewModel.showSuccessDialog = true
                                        }else{

                                        }
                            } }

                          //
                        }
                    )
                }
                    if (isUploading) {
                        Log.w("Success","Comes $isUploading")
                        /*// Simple Progress Indicator with message
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Uploading photos...")
                        }*/

                        AlertDialog(
                            onDismissRequest = {}, // Prevent dismiss
                            title = null, // Remove default title to center content properly
                            text = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "Registration started please wait...",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            },
                            confirmButton = {} // No buttons
                        )
                    }

                if (viewModel.showSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { /* Block dismiss by outside click */ },
                        title = { Text("Success") },
                        text = { Text(viewModel.successMessage) },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.clearForm()
                                viewModel.showSuccessDialog = false
                            }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }

        }
    }

}

@Composable
fun SuccessDialog(
    message: String = "Submission Successful!",
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(300)) +
                        scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier
                        .widthIn(max = 300.dp) // center with fixed width
                        .wrapContentHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .animateContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = onDismiss) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun PhotoQuestionSection(
    questionText: String,
    photo1Bitmap: Bitmap?,
    photo2Bitmap: Bitmap?,
    onFrontPhotoGalleryClick: () -> Unit,
    onFrontPhotoCameraCaptured: (Bitmap) -> Unit, // receives captured image
    onFrontPhotoRemove: () -> Unit,
    onBackPhotoGalleryClick: () -> Unit,
    onBackPhotoCameraCaptured: (Bitmap) -> Unit,
    onBackPhotoRemove: () -> Unit
) {
    val context = LocalContext.current
    val pendingCameraFor = remember { mutableStateOf<Int?>(null) } // 1 or 2

    val takePicture1Launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { onFrontPhotoCameraCaptured(it) }
    }

    val takePicture2Launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { onBackPhotoCameraCaptured(it) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Launch the camera for the pending slot only once
            when (pendingCameraFor.value) {
                1 -> takePicture1Launcher.launch()
                2 -> takePicture2Launcher.launch()
            }
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
        pendingCameraFor.value = null
    }

    fun launchCameraFor(slot: Int) {
        // single entry point to request permission or launch camera
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if (slot == 1) takePicture1Launcher.launch()
            else takePicture2Launcher.launch()
        } else {
            pendingCameraFor.value = slot
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text(text = questionText, style = MaterialTheme.typography.titleMedium, color = Color.Black)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column {
                Text(text = "Front", style = MaterialTheme.typography.titleSmall, color = Color.Black)
                PhotoUploadBox(
                    bitmap = photo1Bitmap,
                    onGalleryClick = onFrontPhotoGalleryClick,
                    onCameraClick = { launchCameraFor(1) }, // DO NOT call any other camera launcher from parent
                    onRemove = onFrontPhotoRemove
                )
            }

            Column {
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black
                )
                PhotoUploadBox(
                    bitmap = photo2Bitmap,
                    onGalleryClick = onBackPhotoGalleryClick,
                    onCameraClick = { launchCameraFor(2) },
                    onRemove = onBackPhotoRemove
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selected: String?,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}




@Composable
fun <T> AnimatedDropdown(
    label: String,
    items: List<T>,
    selectedItem: T?,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = selectedItem?.let { "${label}: ${itemLabel(it)}" } ?: label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = LocalIndication.current,
                    interactionSource = remember { MutableInteractionSource() }
                ) { expanded = !expanded }
                .padding(12.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small)
                .padding(12.dp)
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                items(items.size) { index ->
                    val item = items[index]
                    Text(
                        text = itemLabel(item),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                indication = LocalIndication.current,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onItemSelected(item)
                                expanded = false
                            }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun AnimatedDropdownOld(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    onOptionSelectedPosition:(Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.titleMedium, color = Color.Black)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .border(
                    width = 1.dp,
                    color = if (expanded) MaterialTheme.colorScheme.primary else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(
                    indication = LocalIndication.current,
                    interactionSource = remember { MutableInteractionSource() }
                ) { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedOption ?: "Select $label",
                    color = if (selectedOption == null) Color.Gray else Color.Unspecified
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                options.forEachIndexed { index, option ->
                    Text(
                        text = option,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                indication = LocalIndication.current,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onOptionSelected(option)
                                onOptionSelectedPosition(index)
                                expanded = false
                            }
                            .padding(16.dp)
                    )
                    Divider()
                }
            }
        }
    }
}


@Composable
fun PhotoUploadBox(
    bitmap: Bitmap?, // using Bitmap from TakePicturePreview
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFEFEF)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove photo",
                    tint = Color.White,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50))
                        .padding(2.dp)
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onGalleryClick) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                    }
                    IconButton(onClick = onCameraClick) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Camera")
                    }
                }
            }
        }
    }
}


fun Bitmap.toUri(context: Context): Uri {
    val path = MediaStore.Images.Media.insertImage(
        context.contentResolver, this, "CapturedImage", null
    )
    return Uri.parse(path)
}

@Composable
fun ToastExample(message: String) {
    val context = LocalContext.current

    Button(onClick = {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }) {
        Text("Show Toast")
    }
}


fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF1C21B),
                            Color(0xFF7B3D1C)
                        )
                    )
                )
        )


    }
}


fun uriToBitMap(uri: Uri,context : Context):Bitmap{
    uri.let {
        //   val context = LocalContext.current
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, it)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, it)
            ImageDecoder.decodeBitmap(source)
        }

        return bitmap
    }
}



