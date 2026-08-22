package com.empiretycoon.idleconquest.game

data class ManagerDefinition(val id:String,val name:String,val businessId:String,val role:String,val unlockLevel:Int,val cost:Double,val incomeMultiplier:Double)

data class ManagerState(val definition:ManagerDefinition,val hired:Boolean){
    fun isAvailable(businessLevel:Int)=!hired&&businessLevel>=definition.unlockLevel
}

object ManagerCatalog {
    val all=listOf(
        ManagerDefinition("mia_flux","Mia Flux","street_stand","Street Strategist",15,750.0,1.5),
        ManagerDefinition("noah_vector","Noah Vector","corner_shop","Retail Analyst",40,9_000.0,1.75),
        ManagerDefinition("aya_forge","Aya Forge","workshop","Chief Engineer",100,85_000.0,2.0),
        ManagerDefinition("rex_nova","Rex Nova","factory","Plant Director",250,750_000.0,2.5)
    )
}
