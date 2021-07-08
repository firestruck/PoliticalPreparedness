package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await

class PoliticalPreparednessRepository(val db: ElectionDatabase) {
    private lateinit var electionT: List<Election>
    private lateinit var mVoterInfoResponse: VoterInfoResponse
    private lateinit var mRepresentativeResponse: RepresentativeResponse

    private val api = CivicsApi.retrofitService

    suspend fun getUpcominElectionOnline() : List<Election>{

        withContext(Dispatchers.IO){
            val electionResponse = api.getElections().await()
            electionT = electionResponse.elections
        }
        return electionT
    }

    suspend fun getVoterInfo(adress: String, id: Int) : VoterInfoResponse {
        withContext(Dispatchers.IO){
            val voterInfoResponse = api.getVoterInfo(adress, id).await()
            mVoterInfoResponse = voterInfoResponse
        }
        return mVoterInfoResponse
    }

    suspend fun getSavedElections() : List<Election> {
        return db.electionDao.getElections()
    }

    suspend fun saveElection(election: Election){
        db.electionDao.insert(election)
    }

    fun getElection(id: Int) : Boolean{
        val election = db.electionDao.getElectionById(id)
        election?.let {
            return election.name.isNotEmpty()
        }
        return false
    }

    suspend fun deleteElection(electionId: Int){
        db.electionDao.deleteById(electionId)
    }


    suspend fun getRepresentatives(address: String) : RepresentativeResponse {
        withContext(Dispatchers.IO){
            val representativeResponse = api.getRepresentatives(address).await()
            mRepresentativeResponse = representativeResponse
        }
        return mRepresentativeResponse
    }

}