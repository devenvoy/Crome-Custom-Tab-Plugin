package com.Demo.xmlHybridCompose

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.Demo.xmlHybridCompose.databinding.ActivityMainBinding
import com.saurabharora.customtabs.ConnectionCallback
import com.saurabharora.customtabs.CustomTabActivityHelper2
import com.saurabharora.customtabs.CustomTabFallback
import com.saurabharora.customtabs.extensions.getPackageName
import com.saurabharora.customtabs.extensions.launchWithFallback


class MainActivity : AppCompatActivity(), ConnectionCallback {

    val urlList = listOf(
        "https://earnonline1001.gamesdonut.com/view-game/Tap-Gallery",
        "https://earnonline1001.gamesdonut.com/view-game/Color-Block-Builder",
        "https://earnonline1001.gamesdonut.com/view-game/Unstack-Tower",
        "https://earnonline1001.gamesdonut.com/view-game/Sort-Out",
        "https://earnonline1001.gamesdonut.com/view-game/Tower-Pop",
        "https://earnonline1001.gamesdonut.com/view-game/Tile-Stack"
    )

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val customTabActivityHelper: CustomTabActivityHelper2 =
        CustomTabActivityHelper2(this, lifecycle, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        /* val customizeChromeIntent = CustomTabsIntent.Builder()
             // set Color of the Toolbar
             .setDefaultColorSchemeParams(
                 CustomTabColorSchemeParams.Builder()
                     .setToolbarColor(ContextCompat.getColor(this, R.color.black))
                     .build()
             )
             // set Dark mode theme
             .setColorSchemeParams(
                 CustomTabsIntent.COLOR_SCHEME_DARK, CustomTabColorSchemeParams.Builder()
                     .setToolbarColor(ContextCompat.getColor(this, R.color.white))
                     .build()
             )
             // Auto Hide App Bar
             .setUrlBarHidingEnabled(true)
             // Show Title
             .setShowTitle(true)
             .build()
 */
        val customTabsIntent =
            CustomTabsIntent.Builder(customTabActivityHelper.session)
                // set Color of the Toolbar
                .setDefaultColorSchemeParams(
                    CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(ContextCompat.getColor(this, R.color.black))
                        .build()
                )
                // set Dark mode theme
                .setColorSchemeParams(
                    CustomTabsIntent.COLOR_SCHEME_DARK, CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(ContextCompat.getColor(this, R.color.white))
                        .build()
                )
                // Auto Hide App Bar
                .setInitialActivityHeightPx(800) // Start as a bottom sheet
                .setShareState(CustomTabsIntent.SHARE_STATE_ON) // Disable share button
                .setActionButton(
                    ContextCompat.getDrawable(this, R.drawable.ic_check)?.toBitmap()!!,
                    "Share",
                    PendingIntent.getActivity(
                        this@MainActivity, 200, Intent(),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
                .setUrlBarHidingEnabled(true)
                // Show Title
                .setShowTitle(true)
                .build()

        val packageName = customTabsIntent.getPackageName(this)


        binding.composeCheckbox.setContent {
//            var isChecked by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                /*  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier
                          .padding(16.dp)
                  ) {
                      RoundedCornerCheckbox(
                          label = if (isChecked) "Checked" else "Unchecked",
                          isChecked = isChecked,
                          onValueChange = { isChecked = it },
                      )
                  }*/

                Spacer(modifier = Modifier.height(40.dp))

                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(urlList) { url ->
                        val uri by remember {
                            mutableStateOf(Uri.parse("https://earnonline1001.gamesdonut.com"))
                        }
                        LaunchedEffect(uri) {
                            customTabActivityHelper.mayLaunchUrl(uri)
                        }
                        Button(
                            modifier = Modifier
                                .padding(10.dp)
                                .height(50.dp),
                            onClick = {
                                customTabsIntent.launchWithFallback(
                                    this@MainActivity,
                                    uri,
                                    packageName,
                                    customTabFallback
                                )
                            }
                        ) {
                            Text(text = url.split("/").last().toString())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color.Blue)
                Spacer(modifier = Modifier.height(40.dp))

                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    itemsIndexed(urlList) { index, url ->
                        val uri by remember {
                            mutableStateOf(Uri.parse("https://554.play.pokiigame.com/"))
                        }
                        LaunchedEffect(uri) {
                            customTabActivityHelper.mayLaunchUrl(uri)
                        }
                        Button(
                            modifier = Modifier
                                .padding(10.dp)
                                .height(50.dp),
                            onClick = {
                                customTabsIntent.launchWithFallback(
                                    this@MainActivity,
                                    uri,
                                    packageName,
                                    customTabFallback
                                )
                            }
                        ) {
                            Text(text = "pokii game ${index + 1}")
                        }
                    }
                }
            }
        }
    }

    private val customTabFallback = object : CustomTabFallback {
        override fun openUri(activity: Activity, uri: Uri) {
            // called when package name not found for custom tabs
        }
    }

    override fun onCustomTabsConnected() {
        // called when custom tabs is connected
    }

    override fun onCustomTabsDisconnected() {
        // called when custom tabs is disconnected
    }
}