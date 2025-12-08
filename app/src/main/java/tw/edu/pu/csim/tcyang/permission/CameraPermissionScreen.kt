package tw.edu.pu.csim.tcyang.permission

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import android.provider.Settings

class CameraPermissionScreen {

}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionScreen(modifier: Modifier = Modifier) {
    val cameraViewModel: CameraPermissionViewModel = viewModel()
    val cameraPermissionState =
        rememberPermissionState(Manifest.permission.CAMERA)

    // 取得 ViewModel 中的權限狀態
    val hasPermission = cameraViewModel.hasCameraPermission

    val context = LocalContext.current

    // 當權限狀態改變時通知 ViewModel
    LaunchedEffect(cameraPermissionState.status) {
        cameraViewModel.updatePermissionStatus(
            cameraPermissionState.status.isGranted)
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (hasPermission) {
            Text(text = "✅ CAMERA 權限已取得")
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)  //啟動相機
            context.startActivity(intent)
            PreviewScreen(modifier)
        } else {
            Text(text = "❌ 尚未取得 CAMERA 權限")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                cameraPermissionState.launchPermissionRequest()
            }) {
                Text("請求 CAMERA 權限")
            }

            if (!cameraPermissionState.status.shouldShowRationale) {
                Button(onClick = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package",
                        context.packageName, null)
                    context.startActivity(intent)
                }) {
                    Text("前往設定 (第一次請求或永久拒絕時)")
                }
            }

        }
    }
}
