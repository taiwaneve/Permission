package tw.edu.pu.csim.tcyang.permission

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

class PreviewScreen {

}
@Composable
fun PreviewScreen(modifier: Modifier = Modifier) {
    val previewViewModel: PreviewViewModel = viewModel()

    val context = LocalContext.current

    //取得目前的生命週期資源
    val lifecycleOwner = LocalLifecycleOwner.current

    // 在 Composable 的生命週期中，只創建一次相機控制器
    val cameraController = remember {
        previewViewModel.setupCameraController(context)
        previewViewModel.cameraController
    }
    // 使用 DisposableEffect 來管理綁定與解綁
    DisposableEffect(lifecycleOwner, cameraController) {

        // 在 Composable 進入時，將相機控制器綁定到生命週期
        cameraController?.bindToLifecycle(lifecycleOwner)

        onDispose {
            // 當 Composable 離開時，解綁並釋放資源
            previewViewModel.releaseCameraController()
        }
    }
    // 顯示相機預覽
    // Box 容器將子元件疊加在一起
    Box(modifier = Modifier.fillMaxSize()) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            // Initialize the PreviewView and configure it
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_START
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                controller = cameraController // Set the controller to manage the camera lifecycle
            }
        }
    )


        Row(
            // 使用 align 將 Row 對齊到 Box 的底部中央
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { previewViewModel.toggleCamera() }
            ) { Text("切換鏡頭") }

            Button(
                onClick = { previewViewModel.zoomIn() }
            ) { Text("放大焦距") }

            Button(
                onClick = { previewViewModel.zoomOut() }
            ) { Text("縮小焦距") }
        }
    }
}

