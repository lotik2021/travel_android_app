package ru.movista.data.framework

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlin.math.abs

class OrientationManager(context: Context) : SensorEventListener {

    companion object {
        const val ERROR_CODE_NEED_CALIBRATION = "sensors_need_calibration"

        private const val UPDATE_PERIOD = 60000 // 60 мс
    }

    var isListeningToOrientationChanges = false

    private val orientationSubject = PublishSubject.create<Notification<Float>>()

    private var sensorManager: SensorManager? = null
    private var sensorRotationVector: Sensor? = null

    private var matrixResult = FloatArray(9)
    private var orientationResult = FloatArray(3)

    private var previousOrientation: Float = 0f

    private var currentOrientation: Float = 0f


    init {
        sensorManager = context.getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        sensorRotationVector = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        if (event.accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) return

        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                // сохраняем предыдущее значение
                previousOrientation = currentOrientation
                // выставляем текущее значение
                setDeviceOrientation(event.values)
                // фильтруем незначительные изменения
                if (abs(currentOrientation - previousOrientation) > 0.8) {
                    orientationSubject.onNext(Notification.createOnNext(currentOrientation))
                }
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun startListenToOrientationChangesIfPossible(): Observable<Notification<Float>>? {
        if (sensorRotationVector == null) {
            return null
        }

        isListeningToOrientationChanges = true

        sensorManager?.registerListener(
            this,
            sensorRotationVector,
            UPDATE_PERIOD // мкс
        )

        return orientationSubject
    }

    fun stopListenToOrientationChanges() {
        isListeningToOrientationChanges = false
        sensorManager?.unregisterListener(this)
    }

    /**
     * определяем текущую ориентацию девайса в пространстве
     */
    private fun setDeviceOrientation(rotationVector: FloatArray) {

        // в matrixResult помещаем матрицу данных о повороте устройства
        SensorManager.getRotationMatrixFromVector(matrixResult, rotationVector)
        // в orientationResult помещаем данные о положении устройства, основываясь на matrixResult
        SensorManager.getOrientation(matrixResult, orientationResult)

        // угол между сев. маг. полюсом и плоскостью экрана устройства, переведенный в градусы
        currentOrientation = Math.toDegrees(orientationResult[0].toDouble()).toFloat()
    }

}