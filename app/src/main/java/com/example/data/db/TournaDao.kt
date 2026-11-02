package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TournaDao {

    // --- User ---
    @Query("SELECT * FROM app_user LIMIT 1")
    fun getCurrentUserFlow(): Flow<AppUser?>

    @Query("SELECT * FROM app_user LIMIT 1")
    suspend fun getCurrentUser(): AppUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCurrentUser(user: AppUser)

    // --- Tournaments ---
    @Query("SELECT * FROM tournaments ORDER BY createdAt DESC")
    fun getAllTournamentsFlow(): Flow<List<Tournament>>

    @Query("SELECT * FROM tournaments WHERE id = :id")
    fun getTournamentByIdFlow(id: String): Flow<Tournament?>

    @Query("SELECT * FROM tournaments WHERE id = :id")
    suspend fun getTournamentById(id: String): Tournament?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: Tournament)

    @Update
    suspend fun updateTournament(tournament: Tournament)

    @Delete
    suspend fun deleteTournament(tournament: Tournament)

    // --- Participants ---
    @Query("SELECT * FROM participants WHERE tournamentId = :tournamentId ORDER BY joinedAt ASC")
    fun getParticipantsFlow(tournamentId: String): Flow<List<Participant>>

    @Query("SELECT * FROM participants WHERE tournamentId = :tournamentId AND status = 'ACTIVE' ORDER BY joinedAt ASC")
    suspend fun getActiveParticipants(tournamentId: String): List<Participant>

    @Query("SELECT * FROM participants WHERE tournamentId = :tournamentId AND userId = :userId LIMIT 1")
    suspend fun getParticipantByUser(tournamentId: String, userId: String): Participant?

    @Query("SELECT * FROM participants WHERE tournamentId = :tournamentId AND userId = :userId LIMIT 1")
    fun getParticipantByUserFlow(tournamentId: String, userId: String): Flow<Participant?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: Participant)

    @Update
    suspend fun updateParticipant(participant: Participant)

    @Delete
    suspend fun deleteParticipant(participant: Participant)

    // --- Matches ---
    @Query("SELECT * FROM tournament_matches WHERE tournamentId = :tournamentId ORDER BY roundNumber ASC, scheduledDate ASC, scheduledTime ASC")
    fun getMatchesFlow(tournamentId: String): Flow<List<TournamentMatch>>

    @Query("SELECT * FROM tournament_matches WHERE player1Id = :userId OR player2Id = :userId ORDER BY scheduledDate DESC")
    fun getMatchesForUserFlow(userId: String): Flow<List<TournamentMatch>>

    @Query("SELECT * FROM tournament_matches WHERE id = :matchId LIMIT 1")
    fun getMatchByIdFlow(matchId: String): Flow<TournamentMatch?>

    @Query("SELECT * FROM tournament_matches WHERE id = :matchId LIMIT 1")
    suspend fun getMatchById(matchId: String): TournamentMatch?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<TournamentMatch>)

    @Update
    suspend fun updateMatch(match: TournamentMatch)

    @Query("DELETE FROM tournament_matches WHERE tournamentId = :tournamentId")
    suspend fun deleteMatchesForTournament(tournamentId: String)

    // --- Group Standings ---
    @Query("SELECT * FROM group_standings WHERE tournamentId = :tournamentId ORDER BY groupName ASC, points DESC, goalDifference DESC, goalsFor DESC")
    fun getStandingsFlow(tournamentId: String): Flow<List<GroupStanding>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStandings(standings: List<GroupStanding>)

    @Update
    suspend fun updateStanding(standing: GroupStanding)

    @Query("DELETE FROM group_standings WHERE tournamentId = :tournamentId")
    suspend fun deleteStandingsForTournament(tournamentId: String)

    // --- National Teams ---
    @Query("SELECT * FROM national_teams ORDER BY nameAr ASC")
    fun getAllNationalTeamsFlow(): Flow<List<NationalTeam>>

    @Query("SELECT * FROM national_teams WHERE continentAr = :continent ORDER BY nameAr ASC")
    fun getNationalTeamsByContinentFlow(continent: String): Flow<List<NationalTeam>>

    @Query("SELECT * FROM national_teams WHERE nameAr LIKE '%' || :query || '%' OR nameEn LIKE '%' || :query || '%' ORDER BY nameAr ASC")
    fun searchNationalTeamsFlow(query: String): Flow<List<NationalTeam>>

    @Query("SELECT * FROM national_teams WHERE id = :id LIMIT 1")
    suspend fun getNationalTeamById(id: String): NationalTeam?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNationalTeams(teams: List<NationalTeam>)

    // --- Complaints & Suggestions ---
    @Query("SELECT * FROM complaints_suggestions ORDER BY createdAt DESC")
    fun getAllComplaintsFlow(): Flow<List<ComplaintSuggestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ComplaintSuggestion)

    @Update
    suspend fun updateComplaint(complaint: ComplaintSuggestion)

    // --- Logs ---
    @Query("SELECT * FROM tournament_logs WHERE tournamentId = :tournamentId ORDER BY timestamp DESC")
    fun getTournamentLogsFlow(tournamentId: String): Flow<List<TournamentLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TournamentLog)
}
