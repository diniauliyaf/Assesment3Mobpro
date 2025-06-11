package com.diniauliya0015.assesment3mobpro.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.diniauliya0015.assesment3mobpro.model.Resep

@Composable
fun DetailDialog(resep: Resep, onDismiss: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = resep.judul)
        },
        text = {
            Column {
                Text(
                    text = "Deskripsi:",
                    fontWeight = FontWeight.Bold
                )
                Text(resep.deskripsi)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Langkah-langkah:",
                    fontWeight = FontWeight.Bold
                )
                Text(resep.langkah)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}