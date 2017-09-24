# Notes and observations
1. Could use H2 (in memory or on disk) for mocking out DB
2. Using Spring Data would reduce amount of repository code(if you use H2 or any other DB)
3. Needs exception mapping separated out of controller. @ExceptionHandler
4. UUID would be a better way to guarantee accounts uniqueness
5. When testing, concerns would be better separated if it was possible to test Controller separately from the Service. Therefore I created a new AccountsControllerMockTests to showcase and do just that. I understand the orginal reason probalby was due to using JSON string for test data, unless you don't trust Jackson of course. Making sure your prod data JSON POJOs are correctly constructed can be done via having copies of those in your tests code build using TDD.
(I haven't created test copy of Transfer myself since I think it would have been overkill for this case.)