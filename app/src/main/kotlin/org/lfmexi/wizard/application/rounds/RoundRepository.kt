package org.lfmexi.wizard.application.rounds

import org.lfmexi.wizard.domain.rounds.Round
import reactor.core.publisher.Mono

interface RoundRepository {
    fun save(round: Round): Mono<Round>
}
