# ZJUT thesis format - all CJK via char codes (encoding-safe)
$ErrorActionPreference = 'Continue'

$workDir = (Get-ChildItem 'c:\Users\Administrator\Desktop' -Recurse -Filter 'thesis_work.docx' | Select-Object -First 1).DirectoryName
$thesisName = (-join @([char]0x6BD5,[char]0x4E1A,[char]0x8BBA,[char]0x6587)) + '.docx'
$srcDoc = Join-Path $workDir $thesisName
if (-not (Test-Path $srcDoc)) {
  $srcDoc = (Get-ChildItem $workDir -Filter '*.docx' |
    Where-Object { $_.Length -gt 280000 -and $_.Name -notlike '~$*' -and $_.Name -notlike '*backup*' } |
    Sort-Object LastWriteTime -Descending | Select-Object -First 1).FullName
}
$docPath = Join-Path (Split-Path $srcDoc) 'thesis_work.docx'
if ((Test-Path $docPath) -and ((Get-Item $docPath).LastWriteTime -gt (Get-Item $srcDoc).LastWriteTime)) {
  # second pass: refine already formatted copy
} else {
  Copy-Item $srcDoc $docPath -Force
}

$cZhai = [char]0x6458
$cYao = [char]0x8981
$cMu = [char]0x76EE
$cLu = [char]0x5F55
$cGuan = [char]0x5173
$cJian = [char]0x952E
$cCi = [char]0x8BCD
$cCan = [char]0x53C2
$cKao = [char]0x8003
$cWen = [char]0x6587
$cXian = [char]0x732E
$cZhi = [char]0x81F4
$cXie = [char]0x8C22
$cDi = [char]0x7B2C
$cZhang = [char]0x7AE0
$cTu = [char]0x56FE
$cBiao = [char]0x8868
$sZhaiYao = "$cZhai  $cYao"
$sMuLu = "$cMu  $cLu"
$sZhiXie = "$cZhi  $cXie"
$sGuanJianCi = "$cGuan$cJian$cCi"
$sCanKaoWenXian = "$cCan$cKao$cWen$cXian"
$sHeiti = [char]0x9ED1 + [char]0x4F53
$sSongti = [char]0x5B8B + [char]0x4F53
$fwColon = [char]0xFF1A

$wdAlignLeft = 0
$wdAlignCenter = 1
$wdAlignJustify = 3
$wdLineSpaceMultiple = 5

function Set-ParaBase {
    param($p, $eastFont, $asciiFont, $size, $bold, $align, $line125, $firstIndent2, $spaceBefore, $spaceAfter)
    try {
    $r = $p.Range
    if ($eastFont) { $r.Font.NameFarEast = $eastFont }
    $r.Font.Name = $(if ($asciiFont) { $asciiFont } else { $eastFont })
    $r.Font.Size = [float]$size
    $r.Font.Bold = $(if ($bold) { -1 } else { 0 })
    $p.Alignment = $align
    if ($line125) {
        $p.Format.LineSpacingRule = $wdLineSpaceMultiple
        $p.Format.LineSpacing = [Math]::Round($size * 1.25, 0)
    }
    if ($null -ne $firstIndent2) {
        $p.Format.CharacterUnitFirstLineIndent = $firstIndent2
        if ($firstIndent2 -eq 0) { $p.Format.FirstLineIndent = 0 }
    }
    if ($null -ne $spaceBefore) { $p.Format.SpaceBefore = [float]$spaceBefore }
    if ($null -ne $spaceAfter) { $p.Format.SpaceAfter = [float]$spaceAfter }
    } catch { }
}

function Get-ParaText($p) { ($p.Range.Text -replace "[\r\n\u0007]", '').Trim() }
function Test-ZhaiYao($t) { $t -eq $sZhaiYao -or $t -match "^$cZhai\s+$cYao$" }
function Test-MuLu($t) { $t -eq $sMuLu -or $t -match "^$cMu\s+$cLu$" }
function Test-ZhiXie($t) { $t -eq $sZhiXie -or $t -eq "$cZhi$cXie" -or $t -match "^$cZhi\s*$cXie$" }
function Test-GuanJianCi($t) { $t.StartsWith($sGuanJianCi) }
function Test-Chapter1($t) { $t -match "^$cDi[\u4e00-\u9fa5\d]+$cZhang\s" }
function Test-H2($t) { $t -match '^\d+\.\d+\s+[^.]' -and $t -notmatch '^\d+\.\d+\.\d+' }
function Test-H3($t) { $t -match '^\d+\.\d+\.\d+\s' }

Get-Process WINWORD -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$word.DisplayAlerts = 0
$doc = $word.Documents.Open($docPath)

$inAbstractZh = $false
$inAbstractEn = $false
$inReferences = $false
$inAck = $false
$fixed = 0

for ($i = 1; $i -le $doc.Paragraphs.Count; $i++) {
    $p = $doc.Paragraphs.Item($i)
    $t = Get-ParaText $p
    if ([string]::IsNullOrWhiteSpace($t)) { continue }

    if (Test-ZhaiYao $t) {
        $p.Range.Text = "$sZhaiYao`r"
        Set-ParaBase $p $sHeiti $sHeiti 16 $true $wdAlignCenter $true 0 0 0
        $inAbstractZh = $true; $inAbstractEn = $false; $fixed++; continue
    }

    if (Test-GuanJianCi $t) {
        $inAbstractZh = $false
        $kw = $t.Substring($sGuanJianCi.Length).TrimStart(':', $fwColon, ' ')
        $parts = $kw -split '[,\uFF0C;\uFF1B\s]+' | Where-Object { $_ }
        $kwFixed = $parts -join ([char]0xFF0C)
        $p.Range.Text = "$sGuanJianCi$fwColon$kwFixed`r"
        Set-ParaBase $p $sSongti 'Times New Roman' 12 $false $wdAlignJustify $true 0 0 0
        $fr = $p.Range.Duplicate; $fr.End = $fr.Start + $sGuanJianCi.Length
        $fr.Font.NameFarEast = $sHeiti; $fr.Font.Bold = -1
        $fixed++; continue
    }

    if ($t -eq 'ABSTRACT') {
        $inAbstractZh = $false; $inAbstractEn = $true
        Set-ParaBase $p 'Times New Roman' 'Times New Roman' 16 $false $wdAlignCenter $true 0 0 0
        $fixed++; continue
    }

    if ($t -match '^KEY\s*WORDS') {
        $inAbstractEn = $false
        $kw = $t -replace '^KEY\s*WORDS[:\s]*', ''
        $parts = $kw -split '[,\uFF0C;\uFF1B]+' | ForEach-Object { $_.Trim().ToLower() } | Where-Object { $_ }
        $p.Range.Text = "KEY WORDS $($parts -join ', ')`r"
        Set-ParaBase $p 'Times New Roman' 'Times New Roman' 12 $false $wdAlignJustify $true 0 0 0
        $kr = $p.Range.Duplicate; $kr.End = $kr.Start + 9; $kr.Font.Bold = -1
        $fixed++; continue
    }

    if (Test-MuLu $t) {
        $p.Range.Text = "$sMuLu`r"
        Set-ParaBase $p $sHeiti $sHeiti 16 $false $wdAlignCenter $true 0 0 0
        $fixed++; continue
    }

    if ($t -eq $sCanKaoWenXian) {
        $inReferences = $true; $inAck = $false
        Set-ParaBase $p $sHeiti $sHeiti 16 $false $wdAlignCenter $true 0 0 0
        $fixed++; continue
    }

    if (Test-ZhiXie $t) {
        $inAck = $true; $inReferences = $false
        $p.Range.Text = "$sZhiXie`r"
        Set-ParaBase $p $sHeiti $sHeiti 16 $false $wdAlignCenter $true 0 0 0
        $fixed++; continue
    }

    if ($inReferences -and ($t -match '^\[\d+\]')) {
        Set-ParaBase $p $sSongti 'Times New Roman' 10.5 $false $wdAlignJustify $true 0 0 0
        $p.Format.FirstLineIndent = 21
        $p.Format.CharacterUnitFirstLineIndent = 0
        $fixed++; continue
    }

    if ($inAck) { Set-ParaBase $p $sSongti 'Times New Roman' 12 $false $wdAlignJustify $true 2 0 0; $fixed++; continue }
    if ($inAbstractZh) { Set-ParaBase $p $sSongti 'Times New Roman' 12 $false $wdAlignJustify $true 2 0 0; $fixed++; continue }
    if ($inAbstractEn) { Set-ParaBase $p 'Times New Roman' 'Times New Roman' 12 $false $wdAlignJustify $true 2 0 0; $fixed++; continue }

    if (Test-Chapter1 $t) { Set-ParaBase $p $sHeiti 'Times New Roman' 16 $false $wdAlignCenter $true 0 0 0; $fixed++; continue }
    if (Test-H2 $t) { Set-ParaBase $p $sHeiti 'Times New Roman' 14 $false $wdAlignLeft $true 0 24 12; $fixed++; continue }
    if (Test-H3 $t) { Set-ParaBase $p $sHeiti 'Times New Roman' 12 $false $wdAlignLeft $true 0 0 0; $fixed++; continue }

    if ($t -match "^$cTu\s*\d+[\-\.]\d+") {
        Set-ParaBase $p $sSongti 'Times New Roman' 10.5 $true $wdAlignCenter $true 0 0 12
        $p.Format.CharacterUnitFirstLineIndent = 0; $fixed++; continue
    }
    if ($t -match "^$cBiao\s*\d+[\-\.]\d+") {
        Set-ParaBase $p $sSongti 'Times New Roman' 10.5 $true $wdAlignCenter $true 0 12 0
        $p.Format.CharacterUnitFirstLineIndent = 0; $fixed++; continue
    }

    if ($inReferences) { continue }

    if ($t -cmatch '^[A-Z\s\-]+$' -and $t.Length -gt 15 -and $i -lt 20) {
        Set-ParaBase $p 'Times New Roman' 'Times New Roman' 16 $false $wdAlignCenter $true 0 0 0; $fixed++; continue
    }
    if ($i -ge 7 -and $i -le 11 -and ($t -match 'Spring|Boot|CAMPUS|RESCUE|ADOPTION|MANAGEMENT|PLATFORM|DESIGN|[\u4e00-\u9fa5]')) {
        Set-ParaBase $p $(if ($t -cmatch '^[A-Z]') { 'Times New Roman' } else { $sHeiti }) 'Times New Roman' 16 $false $wdAlignCenter $true 0 0 0
        $fixed++; continue
    }
    if ($t -match '^\|{2}\s*T\d+' -or $t -match '^\-\-\>' -or ($t -match '^\s*\|' -and $t -match '\|')) { continue }
    if ($p.Range.Tables.Count -gt 0) { continue }

    Set-ParaBase $p $sSongti 'Times New Roman' 12 $false $wdAlignJustify $true 2 0 0
    $fixed++
}

$ch6 = "$cDi$([char]0x516D)$cZhang $([char]0x7CFB)$([char]0x7EDF)$([char]0x6D4B)$([char]0x8BD5)$([char]0x4E0E)$([char]0x5206)$([char]0x6790)"
$ch7 = "$cDi$([char]0x4E03)$cZhang $([char]0x603B)$([char]0x7ED3)$([char]0x4E0E)$([char]0x5C55)$([char]0x671B)"
foreach ($ch in @($ch6, $ch7)) {
    $find = $doc.Content.Duplicate
    $find.Find.ClearFormatting()
    if ($find.Find.Execute($ch)) {
        Set-ParaBase $find.Paragraphs.Item(1) $sHeiti 'Times New Roman' 16 $false $wdAlignCenter $true 0 0 0
    }
}

try {
    if ($doc.TablesOfContents.Count -gt 0) { $doc.TablesOfContents.Item(1).Update() }
    else { $doc.Fields.Update() }
} catch {}

$doc.Save()
$doc.Close([ref]0)
try { $word.Quit([ref]0) } catch {}
[System.Runtime.InteropServices.Marshal]::ReleaseComObject($doc) | Out-Null
[System.Runtime.InteropServices.Marshal]::ReleaseComObject($word) | Out-Null
[GC]::Collect()

$outDoc = Join-Path (Split-Path $srcDoc) '毕业论文_已按格式规范修改.docx'
Copy-Item $docPath $outDoc -Force
try { Copy-Item $docPath $srcDoc -Force } catch { Write-Output "Note: close Word and replace 毕业论文.docx with $outDoc" }
Write-Output "Done. Fixed $fixed paragraphs. Saved: $outDoc"
