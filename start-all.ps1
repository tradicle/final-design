$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$backend = Join-Path $root 'backend'
$frontend = Join-Path $root 'frontend'
$backendPom = Join-Path $backend 'pom.xml'
$frontendPackage = Join-Path $frontend 'package.json'
$frontendNodeModules = Join-Path $frontend 'node_modules'

function Assert-PathExists {
    param(
        [string]$Path,
        [string]$Description
    )

    if (-not (Test-Path -LiteralPath $Path)) {
        throw "$Description not found: $Path"
    }
}

function Assert-CommandExists {
    param([string]$CommandName)

    if (-not (Get-Command $CommandName -ErrorAction SilentlyContinue)) {
        throw "Command not found: $CommandName"
    }
}

try {
    Assert-PathExists -Path $backend -Description 'backend directory'
    Assert-PathExists -Path $frontend -Description 'frontend directory'
    Assert-PathExists -Path $backendPom -Description 'backend pom.xml'
    Assert-PathExists -Path $frontendPackage -Description 'frontend package.json'
    Assert-CommandExists -CommandName 'mvn'
    Assert-CommandExists -CommandName 'npm.cmd'

    $backendCommand = '$Host.UI.RawUI.WindowTitle = "backend"; mvn spring-boot:run'
    $frontendCommand = "if (-not (Test-Path -LiteralPath '$frontendNodeModules')) { npm.cmd install }; `$Host.UI.RawUI.WindowTitle = 'frontend'; npm.cmd run dev"

    Start-Process -FilePath 'powershell.exe' `
        -WorkingDirectory $backend `
        -ArgumentList @('-NoExit', '-ExecutionPolicy', 'Bypass', '-Command', $backendCommand)

    Start-Sleep -Seconds 3

    Start-Process -FilePath 'powershell.exe' `
        -WorkingDirectory $frontend `
        -ArgumentList @('-NoExit', '-ExecutionPolicy', 'Bypass', '-Command', $frontendCommand)

    Write-Host 'Backend and frontend windows started.'
    Write-Host "Backend dir: $backend"
    Write-Host "Frontend dir: $frontend"
}
catch {
    Write-Error $_
    Read-Host 'Start failed. Press Enter to exit'
    exit 1
}
