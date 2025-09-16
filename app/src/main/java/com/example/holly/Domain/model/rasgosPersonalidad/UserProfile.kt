package com.example.holly.Domain.model.rasgosPersonalidad

data class UserProfile(
    val id: String,
    val age: Int,
    val relationshipGoal: RelationType,
    val personalityProfile: PersonalityProfile,
    val interestProfile: InterestProfile,
    val valueProfile: ValueProfile,
    val location: LocationData,
    val profileQuality: ProfileQuality,
    val description: String
)
