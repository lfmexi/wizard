package org.lfmexi.wizard.games.domain.rounds

internal class PlayingPhaseRoundTest {
//    @Test
//    fun `initial player should play a card and update the playing group`() {
//        // given
//        val player = PLAYER_ID_2
//        val round = PLAYING_PHASE_ROUND.copy(
//            roundNumber = NumericValue.ONE,
//            initialPlayer = PLAYER_ID_2,
//            currentPlayer = PLAYER_ID_2
//        )
//
//        val card = ClassCard(RED, NumericValue(13))
//
//        // when
//        val updatedRound = round.registerPlayedCard(player, card)
//
//        assertThat(updatedRound)
//            .usingRecursiveComparison()
//            .ignoringFields("recordedEvents")
//            .isEqualTo(round.copy(
//                currentWinningCard = card,
//                currentWinningPlayer = player,
//                currentPlayer = PLAYER_ID_3,
//                playingCardGroup = RED
//            ))
//
//        updatedRound as PlayingPhaseRound
//
//        assertThat(updatedRound.hasPlayingCardGroup).isTrue()
//    }
//
//    @Test
//    fun `should play a fool card and just pass to the next player`() {
//        // given
//        val player = PLAYER_ID_3
//        val round = PLAYING_PHASE_ROUND.copy(
//            roundNumber = NumericValue.ONE,
//            initialPlayer = PLAYER_ID_2,
//            currentPlayer = PLAYER_ID_3,
//            currentWinningPlayer = PLAYER_ID_2,
//            currentWinningCard = ClassCard(RED, NumericValue(13))
//        )
//
//        val card = FoolCard(RED)
//
//        // when
//        val updatedRound = round.registerPlayedCard(player, card)
//
//        assertThat(updatedRound)
//            .usingRecursiveComparison()
//            .ignoringFields("recordedEvents")
//            .isEqualTo(round.copy(
//                currentWinningPlayer = PLAYER_ID_2,
//                currentPlayer = PLAYER_ID_1
//            ))
//    }
//
//    @Test
//    fun `should beat the current existing card`() {
//        // given
//        val player = PLAYER_ID_3
//        val round = PLAYING_PHASE_ROUND.copy(
//            roundNumber = NumericValue.ONE,
//            initialPlayer = PLAYER_ID_2,
//            currentPlayer = PLAYER_ID_3,
//            currentWinningPlayer = PLAYER_ID_2,
//            currentWinningCard = ClassCard(RED, NumericValue(13)),
//            playingCardGroup = RED,
//            triumphCardGroup = CardGroup.YELLOW
//        )
//
//        val card = ClassCard(CardGroup.YELLOW, NumericValue.ONE)
//
//        // when
//        val updatedRound = round.registerPlayedCard(player, card)
//
//        assertThat(updatedRound)
//            .usingRecursiveComparison()
//            .ignoringFields("recordedEvents")
//            .isEqualTo(round.copy(
//                currentWinningPlayer = PLAYER_ID_3,
//                currentWinningCard = card,
//                currentPlayer = PLAYER_ID_1
//            ))
//    }
//
//    @Test
//    fun `should not be able to beat the current existing card`() {
//        // given
//        val player = PLAYER_ID_3
//        val round = PLAYING_PHASE_ROUND.copy(
//            roundNumber = NumericValue.ONE,
//            initialPlayer = PLAYER_ID_2,
//            currentPlayer = PLAYER_ID_3,
//            currentWinningPlayer = PLAYER_ID_2,
//            currentWinningCard = ClassCard(RED, NumericValue(13)),
//            playingCardGroup = RED,
//            triumphCardGroup = RED
//        )
//
//        val card = ClassCard(CardGroup.YELLOW, NumericValue.ONE)
//
//        // when
//        val updatedRound = round.registerPlayedCard(player, card)
//
//        assertThat(updatedRound)
//            .usingRecursiveComparison()
//            .ignoringFields("recordedEvents")
//            .isEqualTo(round.copy(
//                currentWinningPlayer = PLAYER_ID_2,
//                currentPlayer = PLAYER_ID_1
//            ))
//    }
//
//    @Test
//    fun `should move to the next player without ending the triumph`() {
//        // given
//        val player = PLAYER_ID_3
//        val round = PLAYING_PHASE_ROUND.copy(
//            roundNumber = NumericValue.ONE,
//            initialPlayer = PLAYER_ID_2,
//            currentPlayer = PLAYER_ID_3,
//            currentWinningPlayer = PLAYER_ID_2,
//            currentWinningCard = ClassCard(RED, NumericValue(13)),
//            playingCardGroup = RED,
//            triumphCardGroup = RED
//        )
//
//        val card = ClassCard(CardGroup.YELLOW, NumericValue.ONE)
//
//        // when
//        val updatedRound = round.registerPlayedCard(player, card)
//
//        // then
//        assertThat(updatedRound)
//            .usingRecursiveComparison()
//            .ignoringFields("recordedEvents")
//            .isEqualTo(round.copy(
//                currentWinningPlayer = PLAYER_ID_2,
//                currentPlayer = PLAYER_ID_1
//            ))
//
//        assertThat(updatedRound.recordedEvents).hasSize(1)
//
//        assertThat(updatedRound.recordedEvents.first()).isInstanceOf(
//            PlayingPhaseReadyEvent::class.java
//        )
//    }
//
//    @Test
//    fun `should end the triumph but not the round, setting the current winning as initial`() {
//        // given
//        val player = PLAYER_ID_1
//        val round = PLAYING_PHASE_ROUND.copy(
//            roundNumber = NumericValue(2),
//            initialPlayer = PLAYER_ID_2,
//            currentPlayer = PLAYER_ID_1,
//            currentWinningPlayer = PLAYER_ID_3,
//            currentWinningCard = ClassCard(RED, NumericValue(13)),
//            playingCardGroup = RED,
//            triumphCardGroup = RED,
//            playerScoreBoard = mapOf(
//                PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
//                PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
//                PLAYER_ID_3 to RoundScore(NumericValue.ONE, NumericValue.ZERO)
//            )
//        )
//
//        val card = ClassCard(CardGroup.YELLOW, NumericValue.ONE)
//
//        // when
//        val updatedRound = round.registerPlayedCard(player, card)
//
//        // then
//        assertThat(updatedRound)
//            .usingRecursiveComparison()
//            .ignoringFields("recordedEvents")
//            .isEqualTo(round.copy(
//                currentWinningPlayer = PLAYER_ID_3,
//                currentWinningCard = null,
//                playingCardGroup = null,
//                currentPlayer = PLAYER_ID_3,
//                triumphsPlayed = NumericValue.ONE,
//                playerScoreBoard = mapOf(
//                    PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
//                    PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
//                    PLAYER_ID_3 to RoundScore(NumericValue.ONE, NumericValue.ONE)
//                )
//            ))
//    }
//
//    @Test
//    fun `should end the round`() {
//        // given
//        val player = PLAYER_ID_1
//        val round = PLAYING_PHASE_ROUND.copy(
//            roundNumber = NumericValue.ONE,
//            initialPlayer = PLAYER_ID_2,
//            currentPlayer = PLAYER_ID_1,
//            currentWinningPlayer = PLAYER_ID_2,
//            currentWinningCard = ClassCard(RED, NumericValue(13)),
//            triumphCardGroup = RED,
//            playerScoreBoard = mapOf(
//                PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
//                PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
//                PLAYER_ID_3 to RoundScore(NumericValue.ONE, NumericValue.ZERO)
//            )
//        )
//
//        val card = ClassCard(RED, NumericValue.ONE)
//
//        // when
//        val updatedRound = round.registerPlayedCard(player, card)
//
//        assertThat(updatedRound)
//            .isInstanceOf(EndedRound::class.java)
//            .usingRecursiveComparison()
//            .ignoringFields("recordedEvents")
//            .isEqualTo(
//                EndedRound(
//                    id = round.id,
//                    gameId = round.gameId,
//                    players = round.players,
//                    roundNumber = round.roundNumber,
//                    dealingPlayer = round.dealingPlayer,
//                    initialPlayer = PLAYER_ID_2,
//                    currentPlayer = PLAYER_ID_2,
//                    currentWinningPlayer = PLAYER_ID_2,
//                    currentWinningCard = null,
//                    referenceCardGroup = RED,
//                    triumphsPlayed = NumericValue.ONE,
//                    playerScoreBoard = mapOf(
//                        PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
//                        PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ONE),
//                        PLAYER_ID_3 to RoundScore(NumericValue.ONE, NumericValue.ZERO)
//                    )
//                )
//            )
//
//        assertThat(updatedRound.recordedEvents).hasSize(1)
//        assertThat(updatedRound.recordedEvents.first()).isInstanceOf(
//            RoundEndedEvent::class.java
//        )
//    }
}
