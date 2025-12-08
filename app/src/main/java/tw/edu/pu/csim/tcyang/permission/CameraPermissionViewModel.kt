package tw.edu.pu.csim.tcyang.permission

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CameraPermissionViewModel : ViewModel() {
    // 是否已取得拍照權限
    var hasCameraPermission by mutableStateOf(false)
        private set

    fun updatePermissionStatus(granted: Boolean) {
        hasCameraPermission = granted
    }

}
