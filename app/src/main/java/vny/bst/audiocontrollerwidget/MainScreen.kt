package vny.bst.audiocontrollerwidget

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun MainScreen() {

    val context = LocalContext.current
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/* == 0) {
        1 // to prevent exception when previewing the layout
    } else {
        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }*/

    var imageRes by remember {
        mutableStateOf(
            R.drawable.ic_baseline_volume_up_24
        )
    }

    imageRes = if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT)
        R.drawable.ic_baseline_volume_off_24
    else R.drawable.ic_baseline_volume_up_24

    var sliderValue by remember {
        mutableStateOf(
            (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxVolume.toFloat())
        )
    }

    Log.e("sliderValue", "$sliderValue", )
    Log.e("maxVolume", "$maxVolume", )
    Log.e("streamvolume", "${audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)}")

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Slider(value = sliderValue,
            valueRange = 0f..1f,
            onValueChange = {
                sliderValue = it
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (it * maxVolume).toInt(), 0)
            })

        Spacer(modifier = Modifier.padding(top = 15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT) {
                            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                            imageRes = R.drawable.ic_baseline_volume_up_24
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                && !notificationManager.isNotificationPolicyAccessGranted
                            ) {
                                Intent(
                                    android.provider.Settings
                                        .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                                ).apply {
                                    context.startActivity(this)
                                }
                            } else {
                                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                                imageRes = R.drawable.ic_baseline_volume_off_24
                            }
                        }
                    }
            )
            Spacer(modifier = Modifier.padding(start = 15.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_baseline_vibration_24),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL
                            || audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT
                        ) {
                            audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                        }
                    }
            )
        }
    }

}