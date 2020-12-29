package org.lfmexi.wizard.domain.rounds

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_1
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_2
import org.lfmexi.wizard.domain.Fixtures.PLAYING_PHASE_ROUND
import org.lfmexi.wizard.domain.cards.CardGroup
import org.lfmexi.wizard.domain.cards.ClassCard
import org.lfmexi.wizard.domain.exception.CardNotInHandException
import org.lfmexi.wizard.domain.exception.NotInTurnException
import org.lfmexi.wizard.domain.players.Hand
import org.lfmexi.wizard.domain.players.HandId
import org.lfmexi.wizard.domain.values.NumericValue

internal class PlayingPhaseRoundTest {
    @Test
    fun `should not allow the move since the player is not in turn`() {
        // given
        val player = PLAYER_ID_1
        val round = PLAYING_PHASE_ROUND.copy(
            roundNumber = NumericValue.ONE,
            initialPlayer = PLAYER_ID_2,
            currentPlayer = PLAYER_ID_2
        )

        val card = ClassCard(CardGroup.RED, NumericValue(13))

        val hand = Hand(
            id = HandId.generate(),
            roundId = round.id,
            playerId = player,
            cards = listOf(
                card
            )
        )

        // when
        assertThrows<NotInTurnException> {
            round.playCard(hand, card)
        }
    }

    @Test
    fun `should not allow the move since the card is not in the hand`() {
        // given
        val player = PLAYER_ID_2
        val round = PLAYING_PHASE_ROUND.copy(
            roundNumber = NumericValue.ONE,
            initialPlayer = PLAYER_ID_2,
            currentPlayer = PLAYER_ID_2
        )

        val card = ClassCard(CardGroup.RED, NumericValue(13))

        val hand = Hand(
            id = HandId.generate(),
            roundId = round.id,
            playerId = player,
            cards = listOf(
                ClassCard(CardGroup.YELLOW, NumericValue.ONE)
            )
        )

        // when
        assertThrows<CardNotInHandException> {
            round.playCard(hand, card)
        }
    }
}
