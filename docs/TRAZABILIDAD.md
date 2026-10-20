# Matriz de API pública y escenarios

Los nombres remiten a `test/blackjack/<Clase>Test.java`. Las sobrecargas de reparto se prueban por separado. Las firmas abstractas de Player se implementan y prueban en CPU/Human. Rank añade tests de sus métodos de paquete; Face solo define constantes.

| Unidad | Operación | Escenarios |
|---|---|---|
| Card | `Card` | retainsRank, retainsSuit, initiallyHidden |
| Card | `toString` | hidesIdentity, rendersVisibleCard |
| Card | `getRank` | retainsRank |
| Card | `getSuit` | retainsSuit |
| Card | `setFace` | changesFace, canHideAgain |
| Card | `getFace` | initiallyHidden, changesFace |
| Deck | `Deck` | initiallyEmpty |
| Deck | `takeOneFromTop` | emptyDeckReturnsNull, returnsInFifoOrder, depletionAndRefill |
| Deck | `returnOneToBottom` | returnsInFifoOrder, depletionAndRefill |
| Deck | `shuffle` | shufflePreservesContents |
| Deck | `countElements` | initiallyEmpty, shufflePreservesContents |
| Hand | `Hand` | preservesOwner, preservesIndex, startsEmpty |
| Hand | `getOwner` | preservesOwner |
| Hand | `addElement` | addsCard, hiddenCardHidesHand |
| Hand | `clear` | clearReturnsOriginalCards, clearEmptiesHand, clearResetsAceCount |
| Hand | `getValue` | minimumCardValues |
| Hand | `iterator` | iteratesInInsertionOrder |
| Hand | `countElements` | startsEmpty, addsCard |
| Hand | `isRevealed` | hiddenCardHidesHand, revealChangesVisibility |
| Hand | `reveal` | revealChangesVisibility, revealForwardsToCard |
| Hand | `getIndex` | preservesIndex |
| Hand | `remove` | removesExistingCard, removingAbsentCardDoesNothing, removingAceUpdatesOptimalValue |
| Hand | `toString` | rendersEmptyPrimaryHand, labelsSplitHand, rendersCardText |
| Hand | `getOptimalValue` | promotesOnlyOneAce, aceDemotesAfterHit, bustRemainsBust |
| Hand | `getDuplicatedCards` | equalValuesWithDifferentRanksAreDuplicates, distinctValuesHaveNoDuplicates |
| Hand | `isAllowedHit` | hitPrivilege |
| Hand | `setAllowedHit` | hitPrivilege |
| Hand | `isAllowedSplit` | splitPrivilege |
| Hand | `setAllowedSplit` | splitPrivilege |
| Hand | `isAllowedDouble` | doublePrivilege |
| Hand | `setAllowedDouble` | doublePrivilege |
| Hand | `isAllowedSurrender` | surrenderPrivilege |
| Hand | `setAllowedSurrender` | surrenderPrivilege |
| Hand | `isSurrendered` | surrenderState, surrenderDenied |
| Hand | `setSurrendered` | surrenderState, surrenderDenied |
| Player | `Player` | initialBalance |
| Player | `setMinimalBet` | minimumBet |
| Player | `getMinimalBet` | minimumBet |
| Player | `giveOneCard` | delegatesCard |
| Player | `minValueInHand` | delegatesMinimum |
| Player | `getAccountBalance` | initialBalance, debitUpdatesBalance |
| Player | `revealHand` | delegatesReveal |
| Player | `getOptimalValueInHand` | delegatesOptimum |
| Player | `awardMoney` | awardsMoney, awardRestoresExhaustedBalance |
| Player | `forfeitMoney` | forfeitCappedByBalance, debitUpdatesBalance |
| Player | `getHand` | primaryHand |
| Player | `setAccount` | setAccount |
| Player | `getAccount` | setAccount |
| PlayerCPU | `PlayerCPU` | quitsBelowMinimum, neverSplits |
| PlayerCPU | `play` | standsAtSeventeenOrMore, hitsUntilSeventeen, returnsPlayedHand |
| PlayerCPU | `decideIfQuit` | quitsBelowMinimum |
| PlayerCPU | `nextHand` | neverSplits |
| PlayerCPU | `returnAllCards` | returnsCardsViaHand |
| PlayerHuman | `PlayerHuman` | initialSplitCount, allocatesThreeDistinctHands |
| PlayerHuman | `play` | playsStandWithMockedAction, playReadFailure |
| PlayerHuman | `bet` | validBet, invalidBetIsRetried, exactMinimumDoesNotPrompt, betReadFailure |
| PlayerHuman | `nextHand` | allocatesThreeDistinctHands, refusesFourthSplit |
| PlayerHuman | `decideIfQuit` | quitCommands, retriesInvalidQuit, brokePlayerQuits, quitReadFailure |
| PlayerHuman | `returnAllCards` | resetReleasesSplitSlots, resetClearsEachOccupiedHand |
| PlayerHuman | `countSplitHands` | initialSplitCount, resetReleasesSplitSlots |
| Dealer | `Dealer` | recordsActualDebit, deckCardToHand |
| Dealer | `shuffle` | shuffleDelegatesToDeck |
| Dealer | `collectCards` | collectionHidesCard |
| Dealer | `dealOneCard` | explicitCardToHand, explicitCardToPlayer, deckCardToHand, deckCardToPlayer |
| Dealer | `placeBet` | recordsActualDebit, accumulatesBet |
| Dealer | `compareHands` | comparesScores, surrenderTakesPriority |
| Dealer | `resolveBet` | settlesPlayerShare, settlementIsIdempotent, noBetHasNoPayout |
| Dealer | `getBet` | recordsActualDebit, accumulatesBet |
| BlackJackGame | `BlackJackGame` | zeroFundsEndsWithoutDealing |
| BlackJackGame | `playGame` | zeroFundsEndsWithoutDealing, casinoBlackjackResolvesLoss, resolvesNaturalBlackjack |
| BlackJackGame | `main` | mainClosesInput |
| Suit | `getName` | notation |
| Outcome | `toString` | outcomeText |
| Outcome | `getMultiplier` | potMultiplier |
| ActionHit | `ActionHit` | requestsExactlyOneCard |
| ActionHit | `execute` | requestsExactlyOneCard, deniedHitDoesNotDeal, continuesOnlyPlayableHand |
| ActionDouble | `ActionDouble` | doublesOriginalBet |
| ActionDouble | `execute` | doublesOriginalBet, dealsOneCard, deniedDoubleDoesNotDeal, disablesHit |
| ActionSplit | `ActionSplit` | forksIntoTwoHands |
| ActionSplit | `execute` | forksIntoTwoHands, insufficientFunds, absentDuplicate, exhaustedHands |
| ActionStand | `execute` | terminatesHand, repeatIsIdempotent |
| ActionSurrender | `ActionSurrender` | requestsSurrender |
| ActionSurrender | `execute` | requestsSurrender, terminatesHand |
