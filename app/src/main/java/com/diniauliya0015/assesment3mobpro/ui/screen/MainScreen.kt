package com.diniauliya0015.assesment3mobpro.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.diniauliya0015.assesment3mobpro.BuildConfig
import com.diniauliya0015.assesment3mobpro.R
import com.diniauliya0015.assesment3mobpro.model.Resep
import com.diniauliya0015.assesment3mobpro.model.User
import com.diniauliya0015.assesment3mobpro.network.ResepApi
import com.diniauliya0015.assesment3mobpro.network.UserDataStore
import com.diniauliya0015.assesment3mobpro.ui.theme.Assesment3MobproTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.diniauliya0015.assesment3mobpro.network.ApiStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    val viewModel: MainViewModel = viewModel()
    val errorMessage by viewModel.errorMessage

    var showDialog by remember { mutableStateOf(false) }

    var showResepDialog by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }

    var selectedResep by remember { mutableStateOf<Resep?>(null) }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap = getCroppedImage(context.contentResolver, it)
        if (bitmap != null) showResepDialog = true
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()){
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        }
                        else {
                            showDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.account_circle),
                            contentDescription = stringResource(R.string.profil),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {val options = CropImageContractOptions(
                null, CropImageOptions(
                    imageSourceIncludeGallery = false,
                    imageSourceIncludeCamera = true,
                    fixAspectRatio = true
                )
            )
                launcher.launch(options)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.tambah_resep)
                )
            }
        }
    ) { innerPadding ->
        ScreenContent(viewModel, user.email, Modifier.padding(innerPadding),
            onDelete = { resep -> selectedResep = resep
                showDeleteDialog = true
            })

        if(showDialog) {
            ProfilDialog(
                user = user,
                onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog = false
            }
        }

        if (showResepDialog) {
            ResepDialog(
                bitmap = bitmap,
                onDismissRequest = { showResepDialog = false }) { judul, deskripsi, langkah ->
                viewModel.saveData(user.email, judul, deskripsi, langkah, bitmap!!)
                showResepDialog = false
            }
        }

        if (showDeleteDialog) {
            DeleteDialog(onDismissRequest = { showDeleteDialog = false},
                onConfirm = {selectedResep?.let { viewModel.deleteData(user.email, it.id) }
                    showDeleteDialog = false
                })
        }

        if(errorMessage !=null){
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }
}

@Composable
fun ScreenContent(viewModel: MainViewModel, userId: String, modifier: Modifier = Modifier, onDelete: (Resep) -> Unit) {
    val data by viewModel.data
    val status by viewModel.status.collectAsState()

    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedReceipt by remember { mutableStateOf<Resep?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    if (showDetailDialog && selectedReceipt != null) {
        DetailDialog(resep = selectedReceipt!!, onDismiss = { showDetailDialog = false })
    }

    val context = LocalContext.current
    val editLauncher = rememberLauncherForActivityResult(CropImageContract()) {
        editedBitmap = getCroppedImage(context.contentResolver, it)
    }

    if (showEditDialog && selectedReceipt != null) {
        EditReceiptDialog(
            resep = selectedReceipt!!,
            bitmap = editedBitmap,
            onDismissRequest = {
                showEditDialog = false
                editedBitmap = null
            },
            onUpdate = { judul, deskripsi, langkah, bitmap ->
                viewModel.updateData(
                    userId,
                    selectedReceipt!!.id,
                    judul,
                    deskripsi,
                    langkah,
                    bitmap
                )
                showEditDialog = false
                editedBitmap = null
            },
            onChangeImageClick = {
                val options = CropImageContractOptions(
                    null,
                    CropImageOptions(
                        imageSourceIncludeGallery = false,
                        imageSourceIncludeCamera = true,
                        fixAspectRatio = true
                    )
                )
                editLauncher.launch(options)
            }
        )
    }

    LaunchedEffect(userId) {
        viewModel.retrieveData(userId)
    }
    when(status) {
        ApiStatus.LOADING -> {
            Box (
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }

        ApiStatus.SUCCESS -> {
            if (data.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.tidak_ada_data),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyVerticalGrid(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(data) { resep ->
                        ListItem(
                            resep = resep,
                            onDelete = { onDelete(resep) },
                            onDetailClick = { selectedReceipt = it; showDetailDialog = true },
                            onEditClick = { selectedReceipt = it; showEditDialog = true },
                        )
                    }
                }
            }
        }
        ApiStatus.FAILED -> {
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(text = stringResource(id = R.string.error))
                Button(
                    onClick = { viewModel.retrieveData(userId) },
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id =R.string.try_again))
                }
            }
        }
    }
}

@Composable
fun ListItem(resep: Resep, onDelete: () -> Unit, onDetailClick: (Resep) -> Unit, onEditClick: (Resep) -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, Color.Gray)
            .clickable { onDetailClick(resep) },
        contentAlignment = Alignment.BottomCenter
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(ResepApi.getResepUrl(resep.imageId))
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.gambar, resep.judul),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(id = R.drawable.broken_image),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0f, 0f, 0f, 0.5f))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = resep.judul,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = resep.deskripsi,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (resep.mine == "1") {
                    IconButton(onClick = { onEditClick(resep) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { onDelete() },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.hapus),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(result: GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    }
    else{
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }

    val uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    Assesment3MobproTheme {
        MainScreen()
    }
}