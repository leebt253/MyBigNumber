# MyBigNumber - Add2Num

This repository implements both tasks of the Add2Num challenge in Java.

- **Task 1** is `task1-core`, a reusable Maven JAR containing the `MyBigNumber` core.
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

If a command is not found, install the missing tool before continuing. On Windows
10/11, open PowerShell and use the following commands:

Install Git:

```powershell
winget install --id Git.Git --exact --accept-source-agreements --accept-package-agreements
```

Install Java JDK. The JDK provides both `java` and `javac`:

```powershell
winget install --id EclipseAdoptium.Temurin.17.JDK --exact --accept-source-agreements --accept-package-agreements
```

Install Maven for the current Windows user if `mvn` is not found. Run this block
only when Maven is not already installed, and close any running Maven or Spring
Boot process first:

```powershell
$mavenVersion = '3.9.10'
$tools = Join-Path $env:USERPROFILE 'tools'
$mavenHome = Join-Path $tools "apache-maven-$mavenVersion"
$zip = Join-Path $env:TEMP "apache-maven-$mavenVersion-bin.zip"
New-Item -ItemType Directory -Force $tools | Out-Null
Invoke-WebRequest "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip" -OutFile $zip
Expand-Archive $zip -DestinationPath $tools -Force
[Environment]::SetEnvironmentVariable('MAVEN_HOME', $mavenHome, 'User')
$userPath = [Environment]::GetEnvironmentVariable('Path', 'User')
if ($userPath -notlike "*$mavenHome\bin*") {
	[Environment]::SetEnvironmentVariable('Path', "$userPath;$mavenHome\bin", 'User')
}
```

Close and reopen PowerShell so the updated PATH is loaded, then run the four
version commands again. Continue only after `git`, `java`, `javac`, and `mvn`
all return a version.

If all commands work, continue with the clone step. If the project is already open,
run the commands from the repository root, the folder containing `pom.xml`.

## 2. Clone the repository

Clone the public repository once:

```powershell
$projectsRoot = 'D:\Projects' # Change this to any folder you choose.
$clonePath = Join-Path $projectsRoot 'github.com\leebt253\MyBigNumber'
New-Item -ItemType Directory -Force (Split-Path $clonePath) | Out-Null
git clone https://github.com/leebt253/MyBigNumber.git $clonePath
Set-Location $clonePath
```

The path is built from three required parts:

```text
<your projects folder>\github.com\leebt253\MyBigNumber
```

- `<your projects folder>` is selected by the user, for example `D:\Projects`.
- `github.com` identifies the Git server used by the repository.
- `leebt253` is the GitHub account from the repository URL.
- `MyBigNumber` is the project name from the repository URL.

For GitLab, replace `github.com` with `gitlab.com` and use the matching repository
URL. On macOS or Linux, use the same structure under a folder such as `~/Projects`.

## 3. Task 1: build, test, and package the core

The core class is `com.challenge.add2num.core.MyBigNumber`. Its required API is:

```java
String sum(String stn1, String stn2)
```

It processes both strings from right to left, adds each pair of digits with the
carry, preserves arbitrary-length precision, records every step, and writes the
calculation history when constructed with a `PrintStream`. The web module uses the
additional callback-enabled calculation API to stream progress.

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

The Task 1 handoff branch is named `core`:

```powershell
git switch core
```

The `main` branch contains the complete Task 1 and Task 2 solution.

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

The Task 2 test suite verifies the calculation job and the SSE stream, including
the number of step events, `50%` and `100%` progress for a two-column example,
the `complete` event, the final result, and the final carry.

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

Create the requested `0.0.1` version as a Git tag after the implementation is complete.
Publish the Task 1 artifact on branch `core` and the complete solution on `main`:

```powershell
git add .
git commit -m "Implement Add2Num Task 1 and Task 2"
git tag 0.0.1
git remote add origin https://github.com/leebt253/MyBigNumber.git
git push -u origin main --tags
git switch -c core
git push -u origin core
```
