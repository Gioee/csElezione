![vi](https://github.com/Gioee/csElezione/assets/48024736/a698ad2b-574d-4cac-8825-a3ebef1b440d)

# csElezione

csElezione is a client-server Java communication example: clients are used by employees to elect their work representative.
- The server can handle simultaneously connections from N(default=4) clients
- The server can accept multiple clients thanks to ExecutorServices made by the newFixedThreadPool Executor's factory method
- Since ExecutorServices are executed concurrently in the background I've used CountDownLatches and Locks to avoid concurrency problems
