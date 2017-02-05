# README #

This repository contains the sources of leaks detection
system. 
It can detect information leaks from local PC
like camera observation, wiretapping, theft of local files and so on.

### Subdirectory`s internals ###

* ./daemon - java code of the daemon process, which runs in background and open socket for communication.
* ./gui - platform dependant GUI`s for daemon
* ./launcher - launcher demo script which demonstrates platform-independent configuration