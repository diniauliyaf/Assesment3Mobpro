package com.diniauliya0015.assesment3mobpro.ui.screen

import android.content.res.Configuration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.diniauliya0015.assesment3mobpro.R
import com.diniauliya0015.assesment3mobpro.ui.theme.Assesment3MobproTheme
@Composable
fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirm:() -> Unit
){
    AlertDialog(
        text = { Text(text = stringResource(id = R.string.pesan_hapus))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.hapus))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest()}) {
                Text(text = stringResource(id = R.string.batal))
            }
        },
        onDismissRequest = {onDismissRequest()}
    )
}

@Preview(showSystemUi = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
@Composable
fun DeleteDialogPreview(){
    Assesment3MobproTheme {
        DeleteDialog(onDismissRequest = {}, onConfirm = {})
    }
}