package com.example.serializationmodule

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.serializationmodule.model.EventSerializable
import com.example.serializationmodule.serialization.Serialization
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.LocalDate


@RunWith(AndroidJUnit4::class)
class ExportFileSerializationTest {

    private lateinit var context: Context
    private val mockEvents = listOf(
        EventSerializable(
            id = 1,
            eventType = "BIRTHDAY",
            nameContact = "Alice",
            surnameContact = "Johnson",
            originalDate = LocalDate.of(1990, 1, 1).toString(),
            yearMatter = true,
            nextDate = LocalDate.of(2025, 1, 1).toString(),
            notes = "Best friend",
            image = null
        ),
        EventSerializable(
            id = 2,
            eventType = "ANNIVERSARY",
            nameContact = "Bob",
            surnameContact = "Smith",
            originalDate = LocalDate.of(1995, 5, 15).toString(),
            yearMatter = false,
            nextDate = LocalDate.of(2025, 5, 15).toString(),
            notes = "Work",
            image = null
        )
    )

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun exportEventsToCsvToExternalDir_creates_CSV_file_with_correct_content() {
        Serialization.exportEventsToCsvToExternalDir(context, mockEvents)

        val file = File(context.getExternalFilesDir(null), "exported_events.csv")
        assertTrue("CSV file should exist", file.exists())

        val content = file.readText()
        assertTrue("CSV should contain Alice", content.contains("Alice"))
        assertTrue("CSV should contain Bob", content.contains("Bob"))
        assertTrue("CSV should contain event type BIRTHDAY", content.contains("BIRTHDAY"))
    }

    @Test
    fun exportEventsToJsonToExternalDir_creates_JSON_file_with_correct_content() {
        Serialization.exportEventsToJsonToExternalDir(context, mockEvents)

        val file = File(context.getExternalFilesDir(null), "exported_events.json")
        assertTrue("JSON file should exist", file.exists())

        val content = file.readText()
        assertTrue("JSON should contain surname Johnson", content.contains("Johnson"))
        assertTrue("JSON should contain note Work", content.contains("Work"))
        assertTrue("JSON should contain event type ANNIVERSARY", content.contains("ANNIVERSARY"))
    }

    @After
    fun tearDown() {
        File(context.getExternalFilesDir(null), "exported_events.csv").delete()
        File(context.getExternalFilesDir(null), "exported_events.json").delete()
    }
}
