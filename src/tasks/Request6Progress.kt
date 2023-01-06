package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    var result = emptyList<User>()
    for ((idx, repo) in repos.withIndex()) {
        val temp = service
            .getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()
        result = (result + temp).aggregate()
        updateResults(result, idx == repos.lastIndex)
    }
}
