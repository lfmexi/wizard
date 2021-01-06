package org.lfmexi.wizard.domain.rounds

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_1
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_2
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_3
import org.lfmexi.wizard.domain.Fixtures.PLAYING_PHASE_ROUND
import org.lfmexi.wizard.domain.cards.CardGroup
import org.lfmexi.wizard.domain.cards.ClassCard
import org.lfmexi.wizard.domain.cards.FoolCard
import org.lfmexi.wizard.domain.exception.CardNotInHandException
import org.lfmexi.wizard.domain.exception.IllegalMoveException
import org.lfmexi.wizard.domain.exception.NotInTurnException
import org.lfmexi.wizard.domain.players.Hand
import org.lfmexi.wizard.domain.players.HandId
import org.lfmexi.wizard.domain.scoring.RoundScore
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

    @Test
    fun `should not allow the move since the player has a card with a matching card group in the hand`() {
        // given
        val player = PLAYER_ID_1
        val round = PLAYING_PHASE_ROUND.copy(
            roundNumber = NumericValue(2),
            initialPlayer = PLAYER_ID_2,
            currentPlayer = PLAYER_ID_1,
            currentWinningPlayer = PLAYER_ID_3,
            currentWinningCard = ClassCard(CardGroup.RED, NumericValue(13)),
            referenceCardGroup = CardGroup.RED
        )

        val card = ClassCard(CardGroup.YELLOW, NumericValue.ONE)

        val hand = Hand(
            id = HandId.generate(),
            roundId = round.id,
            playerId = player,
            cards = listOf(
                ClassCard(CardGroup.RED, NumericValue.ONE),
                card
            )
        )

        // when - then
        assertThrows<IllegalMoveException> {
            round.playCard(hand, card)
        }
    }

    @Test
    fun `should play a card and update the current winning player`() {
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
                card
            )
        )

        // when
        val updatedRound = round.playCard(hand, card)

        assertThat(updatedRound)
            .usingRecursiveComparison()
            .ignoringFields("recordedEvents")
            .isEqualTo(round.copy(
                currentWinningCard = card,
                currentWinningPlayer = player,
                currentPlayer = PLAYER_ID_3
            ))
    }

    @Test
    fun `should play a fool card and just pass to the next player`() {
        // given
        val player = PLAYER_ID_3
        val round = PLAYING_PHASE_ROUND.copy(
            roundNumber = NumericValue.ONE,
            initialPlayer = PLAYER_ID_2,
            currentPlayer = PLAYER_ID_3,
            currentWinningPlayer = PLAYER_ID_2,
            currentWinningCard = ClassCard(CardGroup.RED, NumericValue(13))
        )

        val card = FoolCard(CardGroup.RED)

        val hand = Hand(
            id = HandId.generate(),
            roundId = round.id,
            playerId = player,
            cards = listOf(
                card
            )
        )

        // when
        val updatedRound = round.playCard(hand, card)

        assertThat(updatedRound)
            .usingRecursiveComparison()
            .ignoringFields("recordedEvents")
            .isEqualTo(round.copy(
                currentWinningPlayer = PLAYER_ID_2,
                currentPlayer = PLAYER_ID_1
            ))
    }

    @Test
    fun `should beat the current existing card`() {
        // given
        val player = PLAYER_ID_3
        val round = PLAYING_PHASE_ROUND.copy(
            roundNumber = NumericValue.ONE,
            initialPlayer = PLAYER_ID_2,
            currentPlayer = PLAYER_ID_3,
            currentWinningPlayer = PLAYER_ID_2,
            currentWinningCard = ClassCard(CardGroup.RED, NumericValue(13)),
            referenceCardGroup = CardGroup.YELLOW
        )

        val card = ClassCard(CardGroup.YELLOW, NumericValue.ONE)

        val hand = Hand(
            id = HandId.generate(),
            roundId = round.id,
            playerId = player,
            cards = listOf(
                card
            )
        )

        // when
        val updatedRound = round.playCard(hand, card)

        assertThat(updatedRound)
            .usingRecursiveComparison()
            .ignoringFields("recordedEvents")
            .isEqualTo(round.copy(
                currentWinningPlayer = PLAYER_ID_3,
                currentWinningCard = card,
                currentPlayer = PLAYER_ID_1
            ))
    }

    @Test
    fun `should not be able to beat the current existing card`() {
        // given
        val player = PLAYER_ID_3
        val round = PLAYING_PHASE_ROUND.copy(
            roundNumber = NumericValue.ONE,
            initialPlayer = PLAYER_ID_2,
            currentPlayer = PLAYER_ID_3,
            currentWinningPlayer = PLAYER_ID_2,
            currentWinningCard = ClassCard(CardGroup.RED, NumericValue(13)),
            referenceCardGroup = CardGroup.RED
        )

        val card = ClassCard(CardGroup.YELLOW, NumericValue.ONE)

        val hand = Hand(
            id = HandId.generate(),
            roundId = round.id,
            playerId = player,
            cards = listOf(
                card
            )
        )

        // when
        val updatedRound = round.playCard(hand, card)

        assertThat(updatedRound)
            .usingRecursiveComparison()
            .ignoringFields("recordedEvents")
            .isEqualTo(round.copy(
                currentWinningPlayer = PLAYER_ID_2,
                currentPlayer = PLAYER_ID_1
            ))
    }

    @Test
    fun `should move to the next player without ending the triumph`() {
        // given
        val player = PLAYER_ID_3
        val round = PLAYING_PHASE_ROUND.copy(
            roundNumber = NumericValue.ONE,
            initialPlayer = PLAYER_ID_2,
            currentPlayer = PLAYER_ID_3,
            currentWinningPlayer = PLAYER_ID_2,
            currentWinningCard = ClassCard(CardGroup.RED, NumericValue(13)),
            referenceCardGroup = CardGroup.RED
        )

        val card = ClassCard(CardGroup.YELLOW, NumericValue.ONE)

        val hand = Hand(
            id = HandId.generate(),
            roundId = round.id,
            playerId = player,
            cards = listOf(
                card
            )
        )

        // when
        val updatedRound = round.playCard(hand, card)

        // then
        assertThat(updatedRound)
            .usingRecursiveComparison()
            .ignoringFields("recordedEvents")
            .isEqualTo(round.copy(
                currentWinningPlayer = PLAYER_ID_2,
                currentPlayer = PLAYER_ID_1
            ))

        assertThat(updatedRound.recordedEvents).hasSize(2)
        assertThat(updatedRound.recordedEvents[0]).isInstanceOf(
            MoveInRoundRegisteredEvent::class.java
        )
            .usingRecursiveComparison()
            .ignoringFields("round")
            .isEqualTo(
                MoveInRoundRegisteredEvent(
                    round = updatedRound as PlayingPhaseRound,
                    hand = hand.copy(
                        cards = hand.cards - card
                    )
                )
            )

        assertThat(updatedRound.recordedEvents[1]).isInstanceOf(
            PlayingPhaseReadyEvent::class.java
        )
    }

    @Test
    fun `should end the triumph but not the round, setting the current winning as initial`() {
        // given
        val player = PLAYER_ID_1
        val round = PLAYING_PHASE_ROUND.copy(
            roundNumber = NumericValue(2),
            initialPlayer = PLAYER_ID_2,
            currentPlayer = PLAYER_ID_1,
            currentWinningPlayer = PLAYER_ID_3,
            currentWinningCard = ClassCard(CardGroup.RED, NumericValue(13)),
            referenceCardGroup = CardGroup.RED,
            playerScoreBoard = mapOf(
                PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                PLAYER_ID_3 to RoundScore(NumericValue.ONE, NumericValue.ZERO)
            )
        )

        val card = ClassCard(CardGroup.YELLOW, NumericValue.ONE)

        val hand = Hand(
            id = HandId.generate(),
            roundId = round.id,
            playerId = player,
            cards = listOf(
                FoolCard(CardGroup.YELLOW),
                card
            )
        )

        // when
        val updatedRound = round.playCard(hand, card)

        // then
        assertThat(updatedRound)
            .usingRecursiveComparison()
            .ignoringFields("recordedEvents")
            .isEqualTo(round.copy(
                currentWinningPlayer = PLAYER_ID_3,
                currentWinningCard = null,
                currentPlayer = PLAYER_ID_3,
                triumphsPlayed = NumericValue.ONE,
                playerScoreBoard = mapOf(
                    PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                    PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                    PLAYER_ID_3 to RoundScore(NumericValue.ONE, NumericValue.ONE)
                )
            ))
    }

    @Test
    fun `should end the round`() {
        // given
        val player = PLAYER_ID_1
        val round = PLAYING_PHASE_ROUND.copy(
            roundNumber = NumericValue.ONE,
            initialPlayer = PLAYER_ID_2,
            currentPlayer = PLAYER_ID_1,
            currentWinningPlayer = PLAYER_ID_2,
            currentWinningCard = ClassCard(CardGroup.RED, NumericValue(13)),
            referenceCardGroup = CardGroup.RED,
            playerScoreBoard = mapOf(
                PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                PLAYER_ID_3 to RoundScore(NumericValue.ONE, NumericValue.ZERO)
            )
        )

        val card = ClassCard(CardGroup.RED, NumericValue.ONE)

        val hand = Hand(
            id = HandId.generate(),
            roundId = round.id,
            playerId = player,
            cards = listOf(
                card
            )
        )

        // when
        val updatedRound = round.playCard(hand, card)

        assertThat(updatedRound)
            .isInstanceOf(EndedRound::class.java)
            .usingRecursiveComparison()
            .ignoringFields("recordedEvents")
            .isEqualTo(
                EndedRound(
                    id = round.id,
                    gameId = round.gameId,
                    players = round.players,
                    roundNumber = round.roundNumber,
                    dealingPlayer = round.dealingPlayer,
                    initialPlayer = PLAYER_ID_2,
                    currentPlayer = PLAYER_ID_2,
                    currentWinningPlayer = PLAYER_ID_2,
                    currentWinningCard = null,
                    referenceCardGroup = CardGroup.RED,
                    triumphsPlayed = NumericValue.ONE,
                    playerScoreBoard = mapOf(
                        PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                        PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ONE),
                        PLAYER_ID_3 to RoundScore(NumericValue.ONE, NumericValue.ZERO)
                    )
                )
            )

        assertThat(updatedRound.recordedEvents).hasSize(2)
        assertThat(updatedRound.recordedEvents[0]).isInstanceOf(
            MoveInRoundRegisteredEvent::class.java
        )
            .usingRecursiveComparison()
            .ignoringFields("round")
            .isEqualTo(
                MoveInRoundRegisteredEvent(
                    round = round,
                    hand = hand.copy(
                        cards = hand.cards - card
                    )
                )
            )

        assertThat(updatedRound.recordedEvents[1]).isInstanceOf(
            RoundEndedEvent::class.java
        )
    }
}
