package com.example.holly.Domain.usecase

import com.example.holly.Domain.model.rasgosPersonalidad.InterestCategory
import com.example.holly.Domain.model.rasgosPersonalidad.InterestProfile
import com.example.holly.Domain.model.rasgosPersonalidad.LocationData
import com.example.holly.Domain.model.rasgosPersonalidad.PersonalityProfile
import com.example.holly.Domain.model.rasgosPersonalidad.PersonalityTrait
import com.example.holly.Domain.model.rasgosPersonalidad.ProfileQuality
import com.example.holly.Domain.model.rasgosPersonalidad.RelationType
import com.example.holly.Domain.model.rasgosPersonalidad.UserProfile
import com.example.holly.Domain.model.rasgosPersonalidad.ValueProfile
import com.example.holly.Domain.model.rasgosPersonalidad.ValueType
import kotlin.math.min

class TextAnalyzer {
    fun analyzePersonality(description: String): PersonalityProfile {
        // Simulación de análisis NLP - en producción usarías ML/NLP real
        val words = description.lowercase().split(" ", ".", ",", "!")

        val extraversionKeywords = listOf("social", "fiesta", "gente", "extrovertido", "hablar")
        val agreeablenessKeywords = listOf("amable", "generoso", "empático", "ayudar", "cariñoso")
        val conscientiousnessKeywords = listOf("organizado", "responsable", "puntual", "metas", "disciplinado")
        val stabilityKeywords = listOf("tranquilo", "estable", "relajado", "paciente", "sereno")
        val opennessKeywords = listOf("creativo", "curioso", "arte", "aventura", "aprender")

        return PersonalityProfile(
            mapOf(
                PersonalityTrait.EXTRAVERSION to calculateTraitScore(words, extraversionKeywords),
                PersonalityTrait.AGREEABLENESS to calculateTraitScore(words, agreeablenessKeywords),
                PersonalityTrait.CONSCIENTIOUSNESS to calculateTraitScore(words, conscientiousnessKeywords),
                PersonalityTrait.EMOTIONAL_STABILITY to calculateTraitScore(words, stabilityKeywords),
                PersonalityTrait.OPENNESS to calculateTraitScore(words, opennessKeywords)
            )
        )
    }

    private fun calculateTraitScore(words: List<String>, keywords: List<String>): Double {
        val matches = words.intersect(keywords.toSet()).size
        return min(0.5 + (matches * 0.1), 1.0) // Base 0.5 + bonus por keywords
    }
}

// Ejemplo de uso
fun coldplay() {
    val analyzer = TextAnalyzer()
    val algorithm = CompatibilityAlgorithm()

    // Crear perfiles de ejemplo
    val user1 = UserProfile(
        id = "user1",
        age = 28,
        relationshipGoal = RelationType.SERIOUS,
        personalityProfile = analyzer.analyzePersonality("Soy una persona tranquila, organizada y me gusta ayudar a otros"),
        interestProfile = InterestProfile(
            interests = mapOf(
                InterestCategory.MUSIC to listOf("rock", "jazz"),
                InterestCategory.SPORTS to listOf("yoga", "running"),
                InterestCategory.READING to listOf("novelas", "ciencia")
            ),
            weights = mapOf(
                InterestCategory.MUSIC to 0.8,
                InterestCategory.SPORTS to 0.9,
                InterestCategory.READING to 0.7
            )
        ),
        valueProfile = ValueProfile(
            values = mapOf(
                ValueType.FAMILY to 0.9,
                ValueType.PERSONAL_GROWTH to 0.8,
                ValueType.HEALTH to 0.9
            )
        ),
        location = LocationData(19.4326, -99.1332, 25.0), // CDMX
        profileQuality = ProfileQuality(4, true, 0.9, 12),
        description = "Busco una relación seria con alguien que comparta mis valores"
    )

    val user2 = UserProfile(
        id = "user2",
        age = 26,
        relationshipGoal = RelationType.NOT_SURE,
        personalityProfile = analyzer.analyzePersonality("Me encanta conocer gente nueva, soy muy social y creativo"),
        interestProfile = InterestProfile(
            interests = mapOf(
                InterestCategory.MUSIC to listOf("rock", "pop"),
                InterestCategory.SPORTS to listOf("yoga", "hiking"),
                InterestCategory.ART to listOf("pintura", "fotografía")
            ),
            weights = mapOf(
                InterestCategory.MUSIC to 0.7,
                InterestCategory.SPORTS to 0.8,
                InterestCategory.ART to 0.9
            )
        ),
        valueProfile = ValueProfile(
            values = mapOf(
                ValueType.CREATIVITY to 0.9,
                ValueType.ADVENTURE to 0.8,
                ValueType.PERSONAL_GROWTH to 0.7
            )
        ),
        location = LocationData(19.4205, -99.1503, 30.0), // CDMX (otra zona)
        profileQuality = ProfileQuality(6, true, 0.85, 24),
        description = "Artista en busca de conexiones auténticas"
    )

    // Calcular compatibilidad
    val compatibility = algorithm.calculateCompatibility(user1, user2)

    println("=== ANÁLISIS DE COMPATIBILIDAD ===")
    println("Puntuación total: ${String.format("%.1f", compatibility.totalScore)}%")
    println("Afinidad de relación: ${String.format("%.1f", compatibility.relationshipAffinityScore)}%")
    println("Compatibilidad de personalidad: ${String.format("%.1f", compatibility.personalityScore)}%")
    println("Intereses compartidos: ${String.format("%.1f", compatibility.interestScore)}%")
    println("Valores alineados: ${String.format("%.1f", compatibility.valueScore)}%")
    println("Proximidad geográfica: ${String.format("%.1f", compatibility.locationScore)}%")
    println("Calidad de perfiles: ${String.format("%.1f", compatibility.qualityScore)}%")
    println("\nExplicación: ${compatibility.explanation}")
}
