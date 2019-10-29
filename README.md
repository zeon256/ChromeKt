# ChromeKt
Dumps google chrome passwords and sends them to your email.

<p align="center">
  <img width="327" height="61" src="./logo.png">
</p>

## Problems
- Jar file is 10mb
- Runs on JVM, -> slow startup. Need to improve startup to run effectively
- Cannot run if target does not have JRE

## How to use?
Clone and import folder from intelliJ. Make sure JDK is installed.

TODO:
- Use substrateVM to compile to native binaries for faster startup time
- Find a way to make a rubber ducky out of compiled binaries
- Maybe even completely move to another language to have a smaller binary LOL (maybe golang?)
