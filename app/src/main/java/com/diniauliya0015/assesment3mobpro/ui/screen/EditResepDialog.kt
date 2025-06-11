package com.diniauliya0015.assesment3mobpro.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.diniauliya0015.assesment3mobpro.R
import com.diniauliya0015.assesment3mobpro.model.Resep

@Composable
fun EditReceiptDialog(
    resep: Resep,
    bitmap: Bitmap?,
    onDismissRequest: () -> Unit,
    onUpdate: (String, String, String, Bitmap?) -> Unit,
    onChangeImageClick: () -> Unit
) {
    var judul by remember { mutableStateOf(resep.judul) }
    var deskripsi by remember { mutableStateOf(resep.deskripsi) }
    var langkah by remember { mutableStateOf(resep.langkah ?: "") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }

                OutlinedButton(
                    onClick = { onChangeImageClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = stringResource(id = R.string.ganti_gambar))
                }

                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text(stringResource(id = R.string.judul)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text(stringResource(id = R.string.deskripsi)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = langkah,
                    onValueChange = { langkah = it },
                    label = { Text(stringResource(id = R.string.langkah)) },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    textStyle = LocalTextStyle.current.copy(
                        lineHeight = 20.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Default,
                        keyboardType = KeyboardType.Text
                    ),
                    singleLine = false,
                    maxLines = Int.MAX_VALUE
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(stringResource(R.string.batal))
                    }
                    OutlinedButton(
                        onClick = {
                            onUpdate(judul, deskripsi, langkah, bitmap)
                        },
                        enabled = judul.isNotEmpty() && deskripsi.isNotEmpty() && langkah.isNotEmpty(),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(stringResource(R.string.simpan))
                    }
                }
            }
        }
    }
}
