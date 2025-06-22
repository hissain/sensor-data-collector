package com.hissain.android.demo.sensordatacollector.model
import android.os.Parcelable
import com.hissain.android.demo.sensordatacollector.config.Configuration
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val name: String = "",
    val age: Int = 0,
    val sex: String = "",
    val height: Float = 0f, // in cm
    val weight: Float = 0f  // in kg
) : Parcelable {

    fun isValid(): Boolean {
        return name.isNotBlank() &&
                age in Configuration.TARGET_AGE_MIN..Configuration.TARGET_AGE_MAX &&
                sex.isNotBlank() &&
                height > 0 &&
                weight > 0
    }
}