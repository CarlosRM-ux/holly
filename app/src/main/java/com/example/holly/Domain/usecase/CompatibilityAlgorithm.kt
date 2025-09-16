package com.example.holly.Domain.usecase

import com.example.holly.Domain.model.rasgosPersonalidad.CompatibilityScore
import com.example.holly.Domain.model.rasgosPersonalidad.InterestCategory
import com.example.holly.Domain.model.rasgosPersonalidad.LocationData
import com.example.holly.Domain.model.rasgosPersonalidad.PersonalityTrait
import com.example.holly.Domain.model.rasgosPersonalidad.ProfileQuality
import com.example.holly.Domain.model.rasgosPersonalidad.RelationType
import com.example.holly.Domain.model.rasgosPersonalidad.UserProfile
import com.example.holly.Domain.model.rasgosPersonalidad.ValueType
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class CompatibilityAlgorithm {
    // Pesos para cada componente del algoritmo
    private val weights = mapOf(
        "relationship" to 0.20,
        "personality" to 0.25,
        "interests" to 0.20,
        "values" to 0.20,
        "location" to 0.10,
        "quality" to 0.05
    )

    // Matrices de compatibilidad para tipos de relación
    private val relationshipCompatibilityMatrix = mapOf(
        RelationType.CASUAL to mapOf(
            RelationType.CASUAL to 0.9,
            RelationType.SERIOUS to 0.2,
            RelationType.FRIENDSHIP to 0.6,
            RelationType.NOT_SURE to 0.7,
            RelationType.OPEN_TO_ANYTHING to 0.8
        ),
        RelationType.SERIOUS to mapOf(
            RelationType.CASUAL to 0.2,
            RelationType.SERIOUS to 0.95,
            RelationType.FRIENDSHIP to 0.4,
            RelationType.NOT_SURE to 0.7,
            RelationType.OPEN_TO_ANYTHING to 0.8
        ),
        RelationType.FRIENDSHIP to mapOf(
            RelationType.CASUAL to 0.6,
            RelationType.SERIOUS to 0.4,
            RelationType.FRIENDSHIP to 0.9,
            RelationType.NOT_SURE to 0.6,
            RelationType.OPEN_TO_ANYTHING to 0.7
        ),
        RelationType.NOT_SURE to mapOf(
            RelationType.CASUAL to 0.7,
            RelationType.SERIOUS to 0.7,
            RelationType.FRIENDSHIP to 0.6,
            RelationType.NOT_SURE to 0.8,
            RelationType.OPEN_TO_ANYTHING to 0.9
        ),
        RelationType.OPEN_TO_ANYTHING to mapOf(
            RelationType.CASUAL to 0.8,
            RelationType.SERIOUS to 0.8,
            RelationType.FRIENDSHIP to 0.7,
            RelationType.NOT_SURE to 0.9,
            RelationType.OPEN_TO_ANYTHING to 0.85
        )
    )

    fun calculateCompatibility(user1: UserProfile, user2: UserProfile): CompatibilityScore {
        val relationshipScore = calculateRelationshipCompatibility(user1, user2)
        val personalityScore = calculatePersonalityCompatibility(user1, user2)
        val interestScore = calculateInterestCompatibility(user1, user2)
        val valueScore = calculateValueCompatibility(user1, user2)
        val locationScore = calculateLocationCompatibility(user1, user2)
        val qualityScore = calculateQualityScore(user1, user2)

        val totalScore = (relationshipScore * weights["relationship"]!! +
                personalityScore * weights["personality"]!! +
                interestScore * weights["interests"]!! +
                valueScore * weights["values"]!! +
                locationScore * weights["location"]!! +
                qualityScore * weights["quality"]!!) * 100

        val explanation = generateExplanation(
            relationshipScore, personalityScore, interestScore,
            valueScore, locationScore, qualityScore
        )

        return CompatibilityScore(
            totalScore = totalScore,
            relationshipAffinityScore = relationshipScore * 100,
            personalityScore = personalityScore * 100,
            interestScore = interestScore * 100,
            valueScore = valueScore * 100,
            locationScore = locationScore * 100,
            qualityScore = qualityScore * 100,
            explanation = explanation
        )
    }

    private fun calculateRelationshipCompatibility(user1: UserProfile, user2: UserProfile): Double {
        return relationshipCompatibilityMatrix[user1.relationshipGoal]?.get(user2.relationshipGoal) ?: 0.5
    }

    private fun calculatePersonalityCompatibility(user1: UserProfile, user2: UserProfile): Double {
        val traits1 = user1.personalityProfile.traits
        val traits2 = user2.personalityProfile.traits

        var compatibility = 0.0
        var totalTraits = 0

        PersonalityTrait.values().forEach { trait ->
            val score1 = traits1[trait] ?: 0.5
            val score2 = traits2[trait] ?: 0.5

            // Algunas combinaciones funcionan mejor con similitud, otras con complementariedad
            val traitCompatibility = when (trait) {
                PersonalityTrait.EXTRAVERSION -> {
                    // Los opuestos pueden atraerse en extroversión
                    val similarity = 1.0 - abs(score1 - score2)
                    val complementarity = abs(score1 - score2)
                    max(similarity * 0.6, complementarity * 0.8)
                }
                PersonalityTrait.EMOTIONAL_STABILITY -> {
                    // Preferimos similitud en estabilidad emocional
                    1.0 - abs(score1 - score2)
                }
                else -> {
                    // Para otros rasgos, un balance entre similitud y complementariedad
                    val similarity = 1.0 - abs(score1 - score2)
                    val complementarity = abs(score1 - score2)
                    similarity * 0.7 + complementarity * 0.3
                }
            }

            compatibility += traitCompatibility
            totalTraits++
        }

        return if (totalTraits > 0) compatibility / totalTraits else 0.5
    }

    private fun calculateInterestCompatibility(user1: UserProfile, user2: UserProfile): Double {
        val interests1 = user1.interestProfile.interests
        val interests2 = user2.interestProfile.interests
        val weights1 = user1.interestProfile.weights
        val weights2 = user2.interestProfile.weights

        var totalCompatibility = 0.0
        var totalWeight = 0.0

        InterestCategory.values().forEach { category ->
            val list1 = interests1[category] ?: emptyList()
            val list2 = interests2[category] ?: emptyList()
            val weight = ((weights1[category] ?: 0.5) + (weights2[category] ?: 0.5)) / 2

            val categoryCompatibility = calculateCategoryCompatibility(list1, list2, category)
            totalCompatibility += categoryCompatibility * weight
            totalWeight += weight
        }

        return if (totalWeight > 0) totalCompatibility / totalWeight else 0.0
    }

    private fun calculateCategoryCompatibility(list1: List<String>, list2: List<String>, category: InterestCategory): Double {
        if (list1.isEmpty() || list2.isEmpty()) return 0.0

        val commonInterests = list1.intersect(list2.toSet()).size
        val totalInterests = (list1 + list2).distinct().size

        val jaccardSimilarity = if (totalInterests > 0) commonInterests.toDouble() / totalInterests else 0.0

        // Algunos intereses son más indicativos de compatibilidad de estilo de vida
        val lifestyleWeight = when (category) {
            InterestCategory.FITNESS, InterestCategory.HEALTH -> 1.2
            InterestCategory.TRAVEL, InterestCategory.ADVENTURE -> 1.1
            InterestCategory.READING, InterestCategory.ART -> 1.0
            else -> 0.9
        }

        return min(jaccardSimilarity * lifestyleWeight, 1.0)
    }

    private fun calculateValueCompatibility(user1: UserProfile, user2: UserProfile): Double {
        val values1 = user1.valueProfile.values
        val values2 = user2.valueProfile.values

        var compatibility = 0.0
        var totalValues = 0

        ValueType.values().forEach { value ->
            val importance1 = values1[value] ?: 0.5
            val importance2 = values2[value] ?: 0.5

            // Los valores fundamentales deben alinearse
            val valueCompatibility = when (value) {
                ValueType.FAMILY, ValueType.LOYALTY -> {
                    // Estos valores requieren alta similitud
                    1.0 - abs(importance1 - importance2)
                }
                else -> {
                    // Otros valores pueden tener más flexibilidad
                    val similarity = 1.0 - abs(importance1 - importance2)
                    val averageImportance = (importance1 + importance2) / 2
                    similarity * 0.7 + averageImportance * 0.3
                }
            }

            compatibility += valueCompatibility
            totalValues++
        }

        return if (totalValues > 0) compatibility / totalValues else 0.5
    }

    private fun calculateLocationCompatibility(user1: UserProfile, user2: UserProfile): Double {
        val distance = calculateDistance(user1.location, user2.location)
        val maxDistance = min(user1.location.maxDistance ?: 50.0, user2.location.maxDistance ?: 50.0)

        return when {
            distance <= 5.0 -> 1.0
            distance <= 15.0 -> 0.9
            distance <= 30.0 -> 0.7
            distance <= maxDistance -> 0.5
            else -> max(0.1, 1.0 - (distance - maxDistance) / maxDistance * 0.4)
        }
    }

    private fun calculateDistance(loc1: LocationData, loc2: LocationData): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(loc2.latitude - loc1.latitude)
        val dLon = Math.toRadians(loc2.longitude - loc1.longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(loc1.latitude)) * cos(Math.toRadians(loc2.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun calculateQualityScore(user1: UserProfile, user2: UserProfile): Double {
        val quality1 = calculateIndividualQuality(user1.profileQuality)
        val quality2 = calculateIndividualQuality(user2.profileQuality)
        return (quality1 + quality2) / 2
    }

    private fun calculateIndividualQuality(quality: ProfileQuality): Double {
        val photoScore = min(quality.photoCount / 5.0, 1.0) * 0.3
        val completenessScore = if (quality.hasCompleteDescription) 0.2 else 0.0
        val profileScore = quality.profileCompleteness * 0.3
        val activityScore = when {
            quality.lastActiveHours <= 24 -> 0.2
            quality.lastActiveHours <= 72 -> 0.15
            quality.lastActiveHours <= 168 -> 0.1
            else -> 0.05
        }

        return photoScore + completenessScore + profileScore + activityScore
    }

    private fun generateExplanation(
        relationship: Double, personality: Double, interests: Double,
        values: Double, location: Double, quality: Double
    ): String {
        val scores = listOf(
            "Objetivos de relación" to relationship,
            "Personalidad" to personality,
            "Intereses" to interests,
            "Valores" to values,
            "Ubicación" to location,
            "Calidad de perfil" to quality
        ).sortedByDescending { it.second }

        val strongPoints = scores.take(2).filter { it.second > 0.7 }
        val weakPoints = scores.takeLast(2).filter { it.second < 0.4 }

        var explanation = "Compatibilidad basada en: "

        if (strongPoints.isNotEmpty()) {
            explanation += "Alta afinidad en ${strongPoints.joinToString(" y ") { it.first.lowercase() }}. "
        }

        if (weakPoints.isNotEmpty()) {
            explanation += "Menor afinidad en ${weakPoints.joinToString(" y ") { it.first.lowercase() }}. "
        }

        return explanation + "El algoritmo considera múltiples factores para sugerir perfiles con potencial de conexión genuina."
    }
}
