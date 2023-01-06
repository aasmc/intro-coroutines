package tasks

import contributors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicInteger

suspend fun loadContributorsChannels(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    coroutineScope {

        val repos = service
            .getOrgRepos(req.org)
            .also { logRepos(req, it) }
            .body() ?: emptyList()

        val channel = Channel<List<User>>()

        repos.map { repo ->
            async(Dispatchers.Default) {
                val lst = service.getRepoContributors(req.org, repo.name)
                    .also { logUsers(repo, it) }
                    .bodyList()
                channel.send(lst)
            }
        }

        var result = emptyList<User>()

        repeat(repos.size) {
            val users = channel.receive()
            result = (result + users).aggregate()
            updateResults(result, it == repos.lastIndex)
        }
    }
}
