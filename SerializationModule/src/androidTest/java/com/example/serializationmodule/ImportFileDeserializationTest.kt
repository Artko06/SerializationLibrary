package com.example.serializationmodule

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.serializationmodule.deserialization.Deserialization
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
class ImportFileDeserializationTest {

    private lateinit var context: Context
    private lateinit var csvUri: Uri
    private lateinit var jsonUri: Uri

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        val csvFile = File(context.getExternalFilesDir(null), "exported_events.csv").apply {
            writeText(
                """
                id,eventType,nameContact,surnameContact,originalDate,yearMatter,notes,image
                1,BIRTHDAY,Alice,Johnson,1990-01-01,true,Best friend,
                2,ANNIVERSARY,Bob,Smith,1995-05-15,false,Work,
                """.trimIndent()
            )
        }
        val jsonFile = File(context.getExternalFilesDir(null), "exported_events.json").apply {
            writeText(
                """
                [
                    {
                        "id": 1,
                        "eventType": "BIRTHDAY",
                        "nameContact": "Alice",
                        "surnameContact": "Johnson",
                        "originalDate": "1990-01-01",
                        "yearMatter": true,
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
                        "notes": "Work",
                        "image": null
                    }
                ]
                """.trimIndent()
            )
        }

        csvUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            csvFile
        )
        jsonUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            jsonFile
        )
    }

    @Test
    fun importEventsFromCsvUri_should_returnCorrectEvents() {
        val events = Deserialization.importEventsFromCsv(context, csvUri)

        assertEquals(2, events.size)

        val alice = events.find { it.nameContact == "Alice" }
        val bob = events.find { it.nameContact == "Bob" }

        assertNotNull(alice)
        assertEquals("BIRTHDAY", alice?.eventType)
        assertEquals("Johnson", alice?.surnameContact)
        assertEquals("Best friend", alice?.notes)

        assertNotNull(bob)
        assertEquals("ANNIVERSARY", bob?.eventType)
        assertEquals("Smith", bob?.surnameContact)
        assertEquals("Work", bob?.notes)
    }

    @Test
    fun importEventsFromJsonUri_should_returnCorrectEvents() {
        val events = Deserialization.importEventsFromJson(context, jsonUri)

        assertEquals(2, events.size)

        val alice = events.find { it.surnameContact == "Johnson" }
        val bob = events.find { it.surnameContact == "Smith" }

        assertNotNull(alice)
        assertEquals("Alice", alice?.nameContact)
        assertEquals("Best friend", alice?.notes)

        assertNotNull(bob)
        assertEquals("Bob", bob?.nameContact)
        assertEquals("Work", bob?.notes)
    }

    @After
    fun tearDown() {
        val dir = context.getExternalFilesDir(null)
        dir?.listFiles()?.forEach { it.delete() }
    }
}
