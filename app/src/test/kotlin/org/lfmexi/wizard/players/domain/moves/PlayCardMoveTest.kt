package org.lfmexi.wizard.players.domain.moves

//
//internal class PlayCardMoveTest {
//    @Test
//    fun `should not allow the move since the player is not in turn`() {
//        // given
//        val player = PLAYER_ID_1
//        val round = PLAYING_PHASE_ROUND.copy(
//            roundNumber = NumericValue.ONE,
//            initialPlayer = PLAYER_ID_2,
//            currentPlayer = PLAYER_ID_2
//        )
//
//        val card = ClassCard(CardGroup.RED, NumericValue(13))
//
//        val hand = Hand(
//            id = HandId.generate(),
//            roundId = round.id,
//            playerId = player,
//            cards = listOf(
//                ClassCard(CardGroup.YELLOW, NumericValue.ONE)
//            )
//        )
//
//        // when
//        assertThrows<NotInTurnException> {
//            PlayCardMove.create(
//                hand = hand,
//                card = card,
//                round = round
//            )
//        }
//    }
//
//    @Test
//    fun `should not allow the move since the card is not in the hand`() {
//        // given
//        val player = PLAYER_ID_2
//        val round = PLAYING_PHASE_ROUND.copy(
//            roundNumber = NumericValue.ONE,
//            initialPlayer = PLAYER_ID_2,
//            currentPlayer = PLAYER_ID_2
//        )
//
//        val card = ClassCard(CardGroup.RED, NumericValue(13))
//
//        val hand = Hand(
//            id = HandId.generate(),
//            roundId = round.id,
//            playerId = player,
//            cards = listOf(
//                ClassCard(CardGroup.YELLOW, NumericValue.ONE)
//            )
//        )
//
//        // when
//        assertThrows<CardNotInHandException> {
//            PlayCardMove.create(hand, card, round)
//        }
//    }
//
//    @Test
//    fun `should not allow the move since the player has a card with a matching card group in the hand`() {
//        // given
//        val player = PLAYER_ID_1
//        val round = PLAYING_PHASE_ROUND.copy(
//            roundNumber = NumericValue(2),
//            initialPlayer = PLAYER_ID_2,
//            currentPlayer = PLAYER_ID_1,
//            currentWinningPlayer = PLAYER_ID_3,
//            currentWinningCard = ClassCard(CardGroup.RED, NumericValue(13)),
//            playingCardGroup = CardGroup.RED,
//            triumphCardGroup = CardGroup.RED
//        )
//
//        val card = ClassCard(CardGroup.YELLOW, NumericValue.ONE)
//
//        val hand = Hand(
//            id = HandId.generate(),
//            roundId = round.id,
//            playerId = player,
//            cards = listOf(
//                ClassCard(CardGroup.RED, NumericValue.ONE),
//                card
//            )
//        )
//
//        // when - then
//        assertThrows<IllegalMoveException> {
//            PlayCardMove.create(hand, card, round)
//        }
//    }
//}
