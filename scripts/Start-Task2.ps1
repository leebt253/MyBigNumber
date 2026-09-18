[CmdletBinding()]
param(
    [ValidateRange(1, 65535)]
    [int]$Port = 8080
)

$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$portInUse = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
if ($portInUse) {
    $ids = ($portInUse | Select-Object -ExpandProperty OwningProcess -Unique) -join ', '
    throw "Port $Port is already in use by process ID(s): $ids. Choose another port with -Port."
}

Push-Location $projectRoot
try {
    mvn -pl task2-web -am install -DskipTests
    if ($LASTEXITCODE -ne 0) { throw "Maven build failed." }
    mvn -f .\task2-web\pom.xml org.springframework.boot:spring-boot-maven-plugin:2.7.18:run "-Dspring-boot.run.arguments=--server.port=$Port"
    if ($LASTEXITCODE -ne 0) { throw "Spring Boot stopped with exit code $LASTEXITCODE." }
}
finally {
    Pop-Location
}
