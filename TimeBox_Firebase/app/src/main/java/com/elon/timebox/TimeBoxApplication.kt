package com.elon.timebox

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * @HiltAndroidApp: Hilt 코드 생성 트리거
 * AndroidManifest.xml의 android:name에 등록 필요
 */
@HiltAndroidApp
class TimeBoxApplication : Application()
