package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey
    val id: String,
    val title: String,
    val gameTitle: String,
    val description: String,
    val ownerId: String,
    val ownerName: String,
    val ownerRoleMode: String, // "PARTICIPANT" or "ORGANIZER_ONLY"
    val maxParticipants: Int = 8,
    val status: String = "REGISTRATION_OPEN",
    val rouletteAttempts: Int = 3,
    val matchDurationMinutes: Int = 10,
    val maxSubstitutions: Int = 6,
    val groupsCount: Int = 2,
    val qualifyPerGroup: Int = 2,
    val hasThirdPlaceMatch: Boolean = true,
    val isDrawPublished: Boolean = false,
    val championId: String? = null,
    val championName: String? = null,
    val championTeam: String? = null,
    val runnerUpName: String? = null,
    val thirdPlaceName: String? = null,
    val telegramChannel: String = "صناعة البطولات",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "participants")
data class Participant(
    @PrimaryKey
    val id: String,
    val tournamentId: String,
    val userId: String,
    val displayName: String,
    val reservedTeamId: String? = null,
    val reservedTeamName: String? = null,
    val reservedTeamFlag: String? = null,
    val rouletteSpinsRemaining: Int = 3,
    val rouletteHistory: String = "",
    val isOwner: Boolean = false,
    val status: String = "ACTIVE", // ACTIVE, WAITING_LIST, DROPPED_OUT
    val hasConsentedRules: Boolean = false,
    val hasConsentedBroadcast: Boolean = false,
    val joinedAt: Long = System.currentTimeMillis(),
    val connectionState: String = "ONLINE", // ONLINE, OFFLINE, RECENT_DISCONNECT
    val disconnectDurationSeconds: Long = 0L
)

@Entity(tableName = "tournament_matches")
data class TournamentMatch(
    @PrimaryKey
    val id: String,
    val tournamentId: String,
    val stage: String, // GROUP, QUARTER, SEMI, THIRD_PLACE, FINAL
    val groupName: String? = null,
    val roundNumber: Int = 1,
    val player1Id: String,
    val player1Name: String,
    val player1Team: String,
    val player1Flag: String = "⚽",
    val player2Id: String,
    val player2Name: String,
    val player2Team: String,
    val player2Flag: String = "⚽",
    val scheduledDate: String = "",
    val scheduledTime: String = "",
    val status: String = "UPCOMING", // UPCOMING, LIVE, COMPLETED, POSTPONED
    val player1Ready: Boolean = false,
    val player2Ready: Boolean = false,
    val player1LineupUri: String? = null,
    val player1LineupApproved: Boolean = false,
    val player1LineupReason: String? = null,
    val player1ExtractedSquad: String? = null,
    val player2LineupUri: String? = null,
    val player2LineupApproved: Boolean = false,
    val player2LineupReason: String? = null,
    val player2ExtractedSquad: String? = null,
    val scorePlayer1: Int? = null,
    val scorePlayer2: Int? = null,
    val submittedByWinnerId: String? = null,
    val isResultApprovedByOwner: Boolean = false,
    val approvalNote: String? = null,
    val hasDispute: Boolean = false,
    val disputeStatus: String = "NONE", // NONE, UNDER_REVIEW, RESOLVED
    val disputeReason: String? = null,
    val disputeEvidenceUri: String? = null,
    val disputeSubmittedById: String? = null,
    val disputeDecision: String? = null,
    val disputeDecisionNote: String? = null,
    val yellowCardsP1: Int = 0,
    val redCardsP1: Int = 0,
    val yellowCardsP2: Int = 0,
    val redCardsP2: Int = 0
)

@Entity(tableName = "group_standings")
data class GroupStanding(
    @PrimaryKey
    val id: String,
    val tournamentId: String,
    val groupName: String,
    val participantId: String,
    val playerName: String,
    val teamName: String,
    val teamFlag: String = "⚽",
    val played: Int = 0,
    val won: Int = 0,
    val drawn: Int = 0,
    val lost: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val goalDifference: Int = 0,
    val points: Int = 0
)

@Entity(tableName = "complaints_suggestions")
data class ComplaintSuggestion(
    @PrimaryKey
    val id: String,
    val userId: String,
    val userName: String,
    val type: String, // COMPLAINT, SUGGESTION, FEATURE_REQUEST
    val title: String,
    val details: String,
    val mediaUri: String? = null,
    val status: String = "NEW", // NEW, UNDER_REVIEW, ACCEPTED, REJECTED, COMPLETED
    val ownerReply: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tournament_logs")
data class TournamentLog(
    @PrimaryKey
    val id: String,
    val tournamentId: String,
    val title: String,
    val details: String,
    val category: String, // SCHEDULE, RESULT, DISPUTE, EXCEPTION, SQUAD
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_user")
data class AppUser(
    @PrimaryKey
    val id: String,
    val displayName: String,
    val email: String,
    val isOwnerEligible: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
