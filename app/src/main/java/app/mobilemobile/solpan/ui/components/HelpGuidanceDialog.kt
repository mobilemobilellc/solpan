/*
 * Copyright 2025 MobileMobile LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package app.mobilemobile.solpan.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import app.mobilemobile.solpan.R

data class LinkInfo(val text: String, val url: String)

@Composable
fun HelpGuidanceDialog(onDismissRequest: () -> Unit) {
  val links =
    listOf(
      LinkInfo("Buy Me a Coffee ☕", "https://buymeacoffee.com/mobilemobile"),
      LinkInfo("View on GitHub", "https://github.com/mobilemobilellc/"),
      LinkInfo("mobilemobile.app", "https://mobilemobile.app"),
    )
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = { Text(stringResource(id = R.string.help_dialog_title)) },
    text = {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
          painter = painterResource(id = R.drawable.happypanel),
          contentDescription = stringResource(id = R.string.help_dialog_graphic_description),
          modifier = Modifier.fillMaxWidth().height(150.dp).padding(vertical = 8.dp),
          contentScale = ContentScale.Fit,
        )
        Text(
          text = stringResource(id = R.string.help_dialog_text_line1),
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(top = 8.dp),
        )
        Text(
          text = stringResource(id = R.string.help_dialog_text_line2),
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(top = 4.dp),
        )
        Text(
          text = stringResource(id = R.string.help_dialog_text_line3),
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(top = 4.dp),
        )
        Text(
          text = stringResource(id = R.string.help_dialog_text_line4),
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        links.forEach { linkInfo ->
          ClickableLink(text = linkInfo.text, url = linkInfo.url)
          Spacer(modifier = Modifier.height(4.dp))
        }
      }
    },
    confirmButton = {
      Button(onClick = onDismissRequest) {
        Text(stringResource(id = R.string.help_dialog_button_dismiss))
      }
    },
  )
}

@Composable
private fun ClickableLink(
  text: String,
  url: String, // Kept for clarity, though not directly used in buildAnnotatedString here
  modifier: Modifier = Modifier,
) {
  Text(
    modifier = modifier,
    text = buildAnnotatedString { withLink(link = LinkAnnotation.Url(url = url)) { append(text) } },
  )
}
