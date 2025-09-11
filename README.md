# CAB302-software-dev-project
Group project for CAB302 Software Development - Semester 2, 2025
# Lachie 11/09 - Run via Maven: Main Menu -> View -> Tool Windows -> Maven -> Execute Maven Goal -> mvn exec:java



## Service API (for UI/math)
- register(username, password) -> User  | errors: Duplicate username
- login(username, password) -> Optional<User>
- startRound(user, mode) -> GameSession (score=0, strikes=0)
- submitCorrect(session): score++
- submitWrong(session): strikes++; auto-finish at 3 strikes
- finishRound(session): marks ended/completed
- highScore(user, mode) -> OptionalInt
- leaderboard(mode, limit) -> List<ScoreRow> (by highScore desc)
- listSessionsByUser(user) -> List<GameSession>
- deleteSession(id) -> boolean
- updateUsername(user, newName) | updatePassword(user, newPw)
- deleteUser(user) -> boolean (cascades sessions)