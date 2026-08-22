package com.empiretycoon.idleconquest.game

import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

data class Milestone(val level:Int,val multiplier:Double)
enum class BuyMode{X1,X10,X25,MAX}
data class UpgradeQuote(val levels:Int,val cost:Double)
data class UpgradeResult(val upgraded:Boolean,val levelsBought:Int=0,val totalCost:Double=0.0,val reachedMilestones:List<Milestone> = emptyList())
data class HireResult(val hired:Boolean,val manager:ManagerDefinition?=null)

data class BusinessState(val id:String,val displayName:String,val level:Int,val baseCost:Double,val baseIncomePerSecond:Double){
 val productionMultiplier:Double get()=milestoneMultiplierFor(level)
 val rawIncomePerSecond:Double get()=baseIncomePerSecond*level.coerceAtLeast(1)*productionMultiplier
 val nextUpgradeCost:Double get()=upgradeCost(level,1)
 fun upgradeCost(fromLevel:Int=level,count:Int):Double{if(count<=0)return 0.0;val first=baseCost*COST_GROWTH.pow(fromLevel.toDouble());return first*(COST_GROWTH.pow(count.toDouble())-1)/(COST_GROWTH-1)}
 fun maxAffordableLevels(cash:Double):Int{if(cash<nextUpgradeCost||!cash.isFinite())return 0;val first=baseCost*COST_GROWTH.pow(level.toDouble());return floor(ln(1+cash*(COST_GROWTH-1)/first)/ln(COST_GROWTH)+1e-10).toInt().coerceAtLeast(0)}
 companion object{const val COST_GROWTH=1.15;private val milestones=listOf(Milestone(25,2.0),Milestone(100,4.0),Milestone(250,8.0),Milestone(500,16.0),Milestone(1000,32.0));fun milestoneMultiplierFor(l:Int)=milestones.lastOrNull{l>=it.level}?.multiplier?:1.0;fun nextMilestoneAfter(l:Int)=milestones.firstOrNull{it.level>l};fun milestonesCrossed(a:Int,b:Int)=milestones.filter{it.level in(a+1)..b}}
}

class GameState{
 var cash=2_500.0;private set
 var gems=12;private set
 private val mutableBusinesses=mutableListOf(BusinessState("street_stand","Street Stand",1,25.0,2.0),BusinessState("corner_shop","Corner Shop",1,160.0,12.0),BusinessState("workshop","Workshop",1,950.0,65.0),BusinessState("factory","Factory",1,5500.0,320.0))
 private val hiredManagerIds=mutableSetOf<String>()
 val businesses:List<BusinessState> get()=mutableBusinesses
 val managers:List<ManagerState> get()=ManagerCatalog.all.map{ManagerState(it,it.id in hiredManagerIds)}
 fun managerFor(businessId:String)=managers.firstOrNull{it.definition.businessId==businessId}
 fun managerMultiplier(businessId:String)=managerFor(businessId)?.takeIf{it.hired}?.definition?.incomeMultiplier?:1.0
 fun incomeFor(b:BusinessState)=b.rawIncomePerSecond*managerMultiplier(b.id)
 val totalIncomePerSecond:Double get()=mutableBusinesses.sumOf{incomeFor(it)}
 fun tick(d:Double){if(d>0)cash+=totalIncomePerSecond*d}
 fun addCash(a:Double){if(a>0&&a.isFinite())cash+=a}
 fun restoreEconomy(cash:Double,gems:Int,levels:Map<String,Int>,hiredManagers:Set<String> = emptySet()){this.cash=cash.coerceAtLeast(0.0);this.gems=gems.coerceAtLeast(0);mutableBusinesses.replaceAll{it.copy(level=levels[it.id]?.coerceAtLeast(1)?:it.level)};hiredManagerIds.clear();hiredManagerIds.addAll(hiredManagers.filter{id->ManagerCatalog.all.any{it.id==id}})}
 fun quoteUpgrade(i:Int,m:BuyMode):UpgradeQuote{val b=mutableBusinesses.getOrNull(i)?:return UpgradeQuote(0,0.0);val n=when(m){BuyMode.X1->1;BuyMode.X10->10;BuyMode.X25->25;BuyMode.MAX->b.maxAffordableLevels(cash)};return UpgradeQuote(n,b.upgradeCost(count=n))}
 fun canUpgrade(i:Int,m:BuyMode=BuyMode.X1)=quoteUpgrade(i,m).let{it.levels>0&&it.cost<=cash}
 fun upgrade(i:Int,m:BuyMode=BuyMode.X1):UpgradeResult{val b=mutableBusinesses.getOrNull(i)?:return UpgradeResult(false);val q=quoteUpgrade(i,m);if(q.levels<=0||q.cost>cash||!q.cost.isFinite())return UpgradeResult(false);cash-=q.cost;val nl=b.level+q.levels;mutableBusinesses[i]=b.copy(level=nl);return UpgradeResult(true,q.levels,q.cost,BusinessState.milestonesCrossed(b.level,nl))}
 fun canHire(managerId:String):Boolean{val m=ManagerCatalog.all.firstOrNull{it.id==managerId}?:return false;val level=businesses.firstOrNull{it.id==m.businessId}?.level?:0;return managerId !in hiredManagerIds&&level>=m.unlockLevel&&cash>=m.cost}
 fun hire(managerId:String):HireResult{val m=ManagerCatalog.all.firstOrNull{it.id==managerId}?:return HireResult(false);if(!canHire(managerId))return HireResult(false);cash-=m.cost;hiredManagerIds+=m.id;return HireResult(true,m)}
 fun hiredManagers():Set<String> = hiredManagerIds.toSet()
 fun tierFor(l:Int)=when{l>=1000->"master";l>=500->"lv500";l>=250->"lv250";l>=100->"lv100";l>=25->"lv25";else->"base"}
}
