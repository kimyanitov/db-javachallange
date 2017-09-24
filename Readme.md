# Notes and observations
1. Could use H2 (in memory or on disk) for mocking out DB
2. Using Spring Data would reduce amount of repository code(if you use H2 or any other DB)
3. Needs exception mapping separated out of controller. @ExceptionHandler
4. UUID would be a better way to guarantee accounts uniqueness
