package com.vitalforge.watch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class Goal(val id: String, val title: String, val target: Int, val unit: String, val progress: Int = 0, val achieved: Boolean = false)
data class Badge(val id: String, val name: String, val description: String, val iconRes: Int, val achievedAt: Long)

class GamificationViewModel : ViewModel() {
    private val _goals = MutableStateFlow(listOf(
        Goal("steady_heart","Steady Heart",5,"events"),
        Goal("sleep_master","Sleep Master",7,"nights"),
        Goal("daily_steps","Daily Steps",10000,"steps")
    ))
    val goals = _goals

    private val _badges = MutableStateFlow(listOf<Badge>())
    val badges = _badges

    fun updateGoalProgress(id: String, amount: Int) {
        _goals.update { list ->
            list.map { goal ->
                if (goal.id == id) {
                    val newProg = (goal.progress + amount).coerceAtMost(goal.target)
                    val achieved = newProg >= goal.target
                    if (achieved && !goal.achieved) awardBadge(goal)
                    goal.copy(progress = newProg, achieved = achieved)
                } else goal
            }
        }
    }

    private fun awardBadge(goal: Goal) {
        val badge = when(goal.id) {
            "steady_heart" -> Badge("badge_steady","Steady Heart","Maintain stable HR",0, System.currentTimeMillis())
            "sleep_master" -> Badge("badge_sleep","Sleep Master","7 nights rest",0, System.currentTimeMillis())
            "daily_steps" -> Badge("badge_steps","Step Champ","10k steps",0, System.currentTimeMillis())
            else -> return
        }
        _badges.update { it + badge }
    }
}
