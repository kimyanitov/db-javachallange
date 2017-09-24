# Assumptions and solutions
1. It is assumed that creation of accounts is working as expected - no modification to that has been made.
2. Account has been split into two (DTO and Entity) mainly for readability.
3. The implementation is agnostic of how the repository may operate. I.e. single mutable entity for all, or new immutable copies each time for each thread or a combination of both.
4. If multi-node solution is required the repository would have to be modified to support that(with some type of locking mechanism). Not done here since is not part of the task.

# Future improvements
1. Debiting and crediting is currently sequential, but could be changed to run in parallel.
2. Since code for debiting and crediting is very simialr - could be nicely refactored using functional programming.
3. More negative scenario testing needed.
 
# Notes and observations
1. Could use H2 (in memory or on disk) for mocking out DB
2. Using Spring Data would reduce amount of repository code(if you use H2 or any other DB)
3. Needs exception mapping separated out of controller. @ExceptionHandler
4. UUID would be a better way to guarantee accounts uniqueness
5. When testing, concerns would be better separated if it was possible to test Controller separately from the Service. Therefore I created a new AccountsControllerMockTests to showcase and do just that. I understand the orginal reason probalby was due to using JSON string for test data, unless you don't trust Jackson of course. Making sure your prod data JSON POJOs are correctly constructed can be done via having copies of those in your tests code build using TDD.
(I haven't created test copy of Transfer myself since I think it would have been overkill for this case.)