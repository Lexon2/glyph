package com.langoverlay.app

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun appContextHasCorrectPackage() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.langoverlay.app", appContext.packageName)
    }

    @Test
    fun mainActivityLaunchesWithoutCrashing() {
        activityRule.scenario.onActivity { activity ->
            assertEquals(MainActivity::class.java, activity::class.java)
        }
    }
}
