# AI assistance record

## Rainbow coin fix — 19 November 2026

- Codex read the coursework brief, inspected the existing game and Git history, and identified the duplicate spawn condition that prevented Rainbow coins from appearing.
- Codex proposed the fix. After approval, it extracted and documented the coin selection rules, connected them to the game controller, and wrote parameterized JUnit tests.
- Codex ran the before-and-after suites. The before run reproduced one expected failure, and the fixed run passed all 31 tests, including the seven direction-validation tests.
- The student saved the test screenshots. Codex reviewed them, checked the branch and diff, reran the suite, and assisted with the commits and pull request.
- The recorded verification covers automated tests. A manual gameplay result has not been confirmed.

## Obstacle placement fix

- Codex reproduced the coordinate and overlap defects with deterministic JUnit tests, implemented placement from free cells, and verified the Silver coin capacity and win-target behavior.
- Codex prepared a temporary copy of baseline commit 4ac2dfb with the original placement rules and the two regression tests. The visible VS Code terminal run reported 33 tests and two expected failures. Its original output is saved in docs/evidence.
- The saved-log screenshots show the original failing and passing Maven runs. The fixed branch passed all 37 JUnit tests, including the existing Rainbow coin and direction-validation tests. Manual gameplay testing has not been confirmed.

## Coin spawning and full-board handling

- Codex traced coin placement and the win path, added a deterministic reproducer for the original retry loop, and implemented the shared free-cell scan and final-coin handling.
- Codex added regression tests for scoring once, continuing when space remains, skipping replacement after a win, and resetting the win target. The before run had one expected failure in 39 tests. The clean fixed run passed all 44 JUnit tests.
- The student captured screenshots from the saved Maven output and reported that all 44 tests passed. Codex verified the evidence files and kept the original Surefire reports outside the repository. Live gameplay was not confirmed.

## Snake collision fix

- Codex traced movement, wraparound, and growth, then wrote deterministic tests that reproduced four missed wrapped obstacle collisions and one missed short-snake self-collision.
- Codex updated movement to check the wrapped landing cell and account for whether the tail vacates it. Additional JUnit cases cover walls, safe wrapping, the Blue coin effect, growth, and collision state after reset.
- The before run reported 49 tests with 5 expected failures. The clean fixed run passed all 63 tests, including the 44 existing tests. The student captured screenshots from the saved Maven output. Manual gameplay testing has not been confirmed.
