package tasks

import contributors.User

/*

 In the initial list each user is present several times, once for each
 repository he or she contributed to.
 Merge duplications: each user should be present only once in the resulting list
 with the total value of contributions for all the repositories.
 Users should be sorted in a descending order by their contributions.

 The corresponding test can be found in test/tasks/AggregationKtTest.kt.
 You can use 'Navigate | Test' menu action (note the shortcut) to navigate to the test.
*/
fun List<User>.aggregate(): List<User> = groupBy { user -> user.login }
    .map { (login, group) ->
        User(login, group.sumOf { it.contributions })
    }.sortedByDescending { it.contributions }

fun List<User>.aggregateAlt(): List<User> {
    val map = hashMapOf<String, Int>()
    for (u in this) {
        map.merge(u.login, u.contributions, Int::plus)
    }
    val result = mutableListOf<User>()
    for ((login, contributions) in map) {
        result.add(User(login, contributions))
    }
    result.sortByDescending { it.contributions }
    return result
}