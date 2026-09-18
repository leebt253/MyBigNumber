# Add2Num

This repository implements both tasks of the Add2Num challenge in Java.

- **Task 1** is `task1-core`, a reusable Maven JAR containing the `LargeNumberAdder` core.
- **Task 2** is `task2-web`, a Spring Boot, Thymeleaf, and Bootstrap web application that consumes the Task 1 JAR.
- The web application reports the real calculation progress through Server-Sent Events.

The input contract follows the requirement document: both parameters are valid strings
containing decimal digits. Input validation is not part of the core algorithm.

## 1. Requirements

- Git
- Java 8 or newer
- Maven 3.6 or newer
- PowerShell on Windows for the provided runner

Check the tools before continuing:

```powershell
git --version
java -version
javac -version
mvn -version
```

If all commands work, continue with the clone step. If the project is already open,
run the commands from the repository root, the folder containing `pom.xml`.

## 2. Clone the repository

Clone the public repository once:

```powershell
git clone https://github.com/leebt253/MyBigNumber.git MyBigNumber
Set-Location .\MyBigNumber
```

For the handoff layout requested by the challenge, a Windows clone path can be:

```text
D:\Projects\github.com\leebt253\MyBigNumber
```

On macOS or Linux, use an equivalent path under `~/Projects`.

## 3. Task 1: build, test, and package the core

The core class is `com.challenge.add2num.core.LargeNumberAdder`. Its public API:

```java
AdditionResult add(String first, String second)
AdditionResult add(String first, String second, StepListener listener)
```

It processes both strings from right to left, adds each pair of digits with the
carry, preserves arbitrary-length precision, records every step, and writes the
calculation history when constructed with a `PrintStream`.

Run Task 1 tests:

```powershell
mvn -pl task1-core clean test
```

Build and install the reusable JAR:

```powershell
mvn -pl task1-core clean install
```

The artifact is created at:

```text
task1-core\target\add2num-core-0.0.1.jar
```

## 4. Task 2: run the web application

Task 2 declares `add2num-core` as a Maven dependency. It does not reimplement the
addition algorithm. The web flow is:

1. Submit two decimal strings.
2. Create a calculation job.
3. Open an SSE progress stream.
4. Receive one event after each processed digit.
5. Display the final result when the core completes.

The default port is `8080`. From the repository root, run:

```powershell
powershell.exe -ExecutionPolicy Bypass -File .\scripts\Start-Task2.ps1
```

Open:

```text
http://localhost:8080/
```

If port `8080` is busy, choose another port yourself:

```powershell
powershell.exe -ExecutionPolicy Bypass -File .\scripts\Start-Task2.ps1 -Port 9090
```

Then open:

```text
http://localhost:9090/
```

The runner builds `task1-core` and `task2-web` before starting Spring Boot.
Stop the server with `Ctrl+C`.

## 5. Test the complete project

Run all Task 1 and Task 2 tests from the repository root:

```powershell
mvn clean test
```

## 6. End-user smoke test

Open the web page and enter:

```text
First number: 95
Second number: 7
```

The expected result is:

```text
102
```

The progress area should show two processed columns and the intermediate carry.
Also test arbitrary-length input:

```text
First number: 999999999999999999999999999999
Second number: 1
```

Expected result:

```text
1000000000000000000000000000000
```

## 7. Acceptance criteria

### Task 1

- `LargeNumberAdder` is a separate reusable core class.
- Addition is performed digit by digit from right to left.
- No fixed-size numeric type or `BigInteger` is used for the calculation.
- Inputs of different lengths and arbitrary-length values are supported.
- Calculation history contains one log entry per digit and a final-carry entry when needed.
- Unit tests cover normal addition, carry, large values, logging, and progress callbacks.
- The core is packaged as `add2num-core-0.0.1.jar`.

### Task 2

- The application uses Spring Boot, Thymeleaf, and Bootstrap.
- Task 2 consumes Task 1 through the `add2num-core` JAR.
- The final result is displayed in the browser.
- Progress is based on completed digit columns: `completed / total * 100`.
- Server-Sent Events update the progress and calculation rows while processing.
- The application and automated tests can be rebuilt from the cloned repository.

## 8. Version handoff

Create the requested `0.0.1` version as a Git tag after the implementation is complete:

```powershell
git add .
git commit -m "Implement Add2Num Task 1 and Task 2"
git tag 0.0.1
git remote add origin https://github.com/leebt253/MyBigNumber.git
git push -u origin main --tags
```
