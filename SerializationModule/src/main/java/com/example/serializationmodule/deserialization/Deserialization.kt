package com.example.serializationmodule.deserialization

import android.content.Context
import android.util.Base64
import com.example.serializationmodule.model.EventSerializable
import java.io.File
import java.io.FileNotFoundException

object Deserialization {
    fun importEventsFromJson(context: Context): List<EventSerializable> {
        return try {
            val file = File(context.getExternalFilesDir(null), "exported_events.json")
            if (!file.exists()) throw FileNotFoundException("File not found in external dir")

            val result = mutableListOf<EventSerializable>()
            val jsonString = file.readText()

            val eventsStr = jsonString
                .trim()
                .removeSurrounding("[", "]")
                .split(Regex("\\},\\s*\\{"))
                .map { it.trim().removePrefix("{").removeSuffix("}") }

            eventsStr.forEach { item ->
                val linesEvent = item.split(",\n").map { it.trim() }
                val mapEvent = mutableMapOf<String, String>()

                for(line in linesEvent) {
                    mapEvent[line.substringBefore(":").removeSurrounding("\"")] =
                        line.substringAfter(":").trim().removeSurrounding("\"")
                }

                println(mapEvent)

                val imageBytes = if (mapEvent["image"].isNullOrEmpty()) null
                else Base64.decode(mapEvent["image"], Base64.NO_WRAP)

                result.add(
                    EventSerializable(
                        id = mapEvent["id"]!!.toInt(),
                        eventType = mapEvent["eventType"]!!,
                        nameContact = mapEvent["nameContact"]!!,
                        surnameContact = mapEvent["surnameContact"],
                        originalDate = mapEvent["originalDate"]!!,
                        yearMatter = mapEvent["yearMatter"]!!.toBoolean(),
                        nextDate = mapEvent["nextDate"]!!,
                        notes = mapEvent["notes"],
                        image = imageBytes
                    )
                )
            }

            result
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun importEventsFromCsvFromExternalDir(context: Context): List<EventSerializable> {
        return try {
            val file = File(context.getExternalFilesDir(null), "exported_events.csv")
            if (!file.exists()) throw FileNotFoundException("File not found in external dir")

            val lines = file.readLines()
            if (lines.isEmpty()) return emptyList()

            val events = mutableListOf<EventSerializable>()

            for (line in lines.drop(1)) { // Skip title
                val tokens = line.split(",")

                if (tokens.size < 9) continue // Skip empty strings

                val id = tokens[0]
                val eventType = tokens[1]
                val nameContact = tokens[2]
                val surnameContact = tokens[3]
                val originalDate = tokens[4]
                val yearMatter = tokens[5].toBoolean()
                val nextDate = tokens[6]
                val notes = tokens[7]
                val imageBase64 = tokens[8]
                val imageBytes = if (imageBase64.isNotBlank()) Base64.decode(imageBase64, Base64.NO_WRAP) else null

                events.add(
                    EventSerializable(
                        id = id.toInt(),
                        eventType = eventType,
                        nameContact = nameContact,
                        surnameContact = surnameContact,
                        originalDate = originalDate,
                        yearMatter = yearMatter,
                        nextDate = nextDate,
                        notes = notes,
                        image = imageBytes
                    )
                )
            }

            events
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}