package com.example.serializationmodule

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.serializationmodule.deserialization.Deserialization
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
class ImportFileDeserializationTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        val csvFile = File(context.getExternalFilesDir(null), "exported_events.csv")
        csvFile.writeText(
            """
            id,eventType,nameContact,surnameContact,originalDate,yearMatter,nextDate,notes,image
            1,BIRTHDAY,Alice,Johnson,1990-01-01,true,2025-01-01,Best friend,
            2,ANNIVERSARY,Bob,Smith,1995-05-15,false,2025-05-15,Work,
            """.trimIndent()
        )

        val jsonFile = File(context.getExternalFilesDir(null), "exported_events.json")
        jsonFile.writeText(
            """
            [
                {
                    "id": 1,
                    "eventType": "BIRTHDAY",
                    "nameContact": "Alice",
                    "surnameContact": "Johnson",
                    "originalDate": "1990-01-01",
                    "yearMatter": true,
                    "nextDate": "2025-01-01",
                    "notes": "Best friend",
                    "image": null
                },
                {
                    "id": 2,
                    "eventType": "ANNIVERSARY",
                    "nameContact": "Bob",
                    "surnameContact": "Smith",
                    "originalDate": "1995-05-15",
                    "yearMatter": false,
                    "nextDate": "2025-05-15",
                    "notes": "Work",
                    "image": null
                }
            ]
            """.trimIndent()
        )
    }

    @Test
    fun importEventsFromCsv_should_return_correct_events() = runBlocking {
        val result = Deserialization.importEventsFromCsvFromExternalDir(context)

        assertEquals(2, result.size)
        assertTrue(result.any { it.nameContact == "Alice" && it.eventType == "BIRTHDAY" })
        assertTrue(result.any { it.nameContact == "Bob" && it.eventType == "ANNIVERSARY" })
    }

    @Test
    fun importEventsFromJson_should_return_correct_events() = runBlocking {
        val result = Deserialization.importEventsFromJson(context)

        assertEquals(2, result.size)
        assertTrue(result.any { it.surnameContact == "Johnson" && it.notes == "Best friend" })
        assertTrue(result.any { it.surnameContact == "Smith" && it.notes == "Work" })
    }

    @After
    fun tearDown() {
        File(context.getExternalFilesDir(null), "exported_events.csv").delete()
        File(context.getExternalFilesDir(null), "exported_events.json").delete()
    }
}

