package tw.edu.pu.csim.tcyang.permission

import android.app.Application
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//class PreviewViewModel : ViewModel() {
class PreviewViewModel(application: Application) : AndroidViewModel(application) {
    // 直接使用 getApplication() 取得 Context
    val context = getApplication<Application>()
    // 保存相機控制器
    var cameraController by
    mutableStateOf<LifecycleCameraController?>(null)
        private set
    // 建立並設定 LifecycleCameraController 的方法
    fun setupCameraController(context: Context) {
        if (cameraController == null) {
            cameraController =
                LifecycleCameraController(context)  // 創建實例
        }
    }

    // 釋放相機資源的方法
    fun releaseCameraController() {
        cameraController?.unbind()
        cameraController = null
    }


    // 保存當前鏡頭選擇的狀態
    var cameraSelector by mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
        private set

    // 切換前後鏡頭的邏輯
    fun toggleCamera() {
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
        // 將新的鏡頭選擇設定給控制器
        cameraController?.cameraSelector = cameraSelector
    }
    var currentZoomRatio by mutableStateOf(1.0f)  // 保存當前焦距，預設為 1.0f
        private set

    fun zoomIn() {       // 放大焦距的邏輯
        //setZoomRatio(...) 可能會花一些時間來完成，需在協程中執行，以免阻塞主執行緒。
        viewModelScope.launch {
            if (currentZoomRatio < cameraController?.zoomState?.value!!.maxZoomRatio){
                currentZoomRatio += 0.1f
            }
            cameraController?.setZoomRatio(currentZoomRatio)
        }
    }

    fun zoomOut() {      // 縮小焦距的邏輯
        viewModelScope.launch {
            if (currentZoomRatio > 1f){  currentZoomRatio -= 0.1f  }
            cameraController?.setZoomRatio(currentZoomRatio)
        }
    }
    // 新增：圖像分析結果的狀態
    //var analysisResult by mutableStateOf("分析結果：等待中...")
    // 手勢辨識結果
    var gestureResult by mutableStateOf("手勢：無")
        private set
    // 持有 HandGestureDetector 的實例
    private var handGestureDetector: HandGestureDetector? = null
    init {
        handGestureDetector = HandGestureDetector(
            context = context,
            scope = viewModelScope,
            onResults = { gesture ->
                // 當 HandGestureDetector 有結果時，更新 ViewModel 的狀態
                gestureResult = "手勢：$gesture"
            }
        )
    }


    // 用於影像分析的執行緒
    private var analysisExecutor: ExecutorService? = null

    // 新增：停止影像分析
    fun stopImageAnalysis() {
        cameraController?.clearImageAnalysisAnalyzer()
    }

    //onCleared() 是 ViewModel 不再被需要時會被系統自動呼叫。
    override fun onCleared() {
        super.onCleared()
        // ViewModel 被清除時，關閉執行緒
        analysisExecutor?.shutdown()
        analysisExecutor = null
    }
    fun startImageAnalysis() {
        // 如果執行緒未初始化，則創建一個
        if (analysisExecutor == null) {
            analysisExecutor = Executors.newSingleThreadExecutor()
        }
        cameraController?.setImageAnalysisAnalyzer(
            analysisExecutor!!,
            ImageAnalysis.Analyzer { imageProxy ->
                // 在這裡處理每一幀圖像的邏輯
                /*
                val width = imageProxy.width
                val height = imageProxy.height
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                // 更新 ViewModel 的狀態
                analysisResult = "圖像 ($width x $height), 旋轉: $rotationDegrees"
                 */
                handGestureDetector?.detect(imageProxy)
                // 完成處理後，必須關閉 ImageProxy
                imageProxy.close()
            }
        )
    }


}

