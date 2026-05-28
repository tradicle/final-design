$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

function S([int[]]$c) { -join ($c | ForEach-Object { [char]$_ }) }

$baseDir = (Get-Item 'c:\Users\Administrator\Desktop\*\insert-flowcharts.ps1').DirectoryName
$flowDir = Join-Path $baseDir (([char]0x8BBA)+([char]0x6587)+([char]0x63D2)+([char]0x56FE)+'\flowcharts')
$p1 = Join-Path $flowDir 'fig3-1-adoption.png'
$p2 = Join-Path $flowDir 'fig3-2-donation.png'
$p3 = Join-Path $flowDir 'fig3-3-community.png'
foreach ($p in @($p1,$p2,$p3)) { if (-not (Test-Path $p)) { throw "Missing $p - run insert-flowcharts.ps1 first for PNG gen" } }

$src = Get-ChildItem $baseDir -Filter '*.docx' | Where-Object { $_.BaseName.EndsWith('2') -and $_.Name -notlike '~$*' } | Select-Object -First 1
$tmpIn  = 'C:\Users\Administrator\AppData\Local\Temp\thesis_edit_in.docx'
$tmpOut = 'C:\Users\Administrator\AppData\Local\Temp\thesis_edit_out.docx'
Copy-Item $src.FullName $tmpIn -Force

$cap1zh = (S 0x56FE) + '3-1  ' + (S 0x9886,0x517B,0x4E1A,0x52A1,0x6D41,0x7A0B,0x56FE)
$cap2zh = (S 0x56FE) + '3-2  ' + (S 0x7269,0x8D44,0x8BA4,0x9886,0x6D41,0x7A0B,0x56FE)
$cap3zh = (S 0x56FE) + '3-3  ' + (S 0x793E,0x533A,0x4E92,0x52A8,0x6D41,0x7A0B,0x56FE)
$blocks = @(
    @{ Img=$p1; CapZh=$cap1zh; CapEn='Figure 3-1  Adoption Business Process' }
    @{ Img=$p2; CapZh=$cap2zh; CapEn='Figure 3-2  Donation Claim Process' }
    @{ Img=$p3; CapZh=$cap3zh; CapEn='Figure 3-3  Community Interaction Process' }
)

Get-Process WINWORD -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$word.DisplayAlerts = 0
$doc = $word.Documents.Open($tmpIn)

function Get-T($p) { ($p.Range.Text -replace "[\r\n\u0007]", '').Trim() }

$pStart=0; $pEnd=$doc.Paragraphs.Count
for ($i=1; $i -le $doc.Paragraphs.Count; $i++) {
    $t = Get-T $doc.Paragraphs.Item($i)
    if ($t -match '^3\.4\.1') { $pStart = $i }
    if ($t -match '^3\.5') { $pEnd = $i; break }
}
$found = [System.Collections.Generic.List[int]]::new()
for ($i=$pStart; $i -lt $pEnd; $i++) {
    if ((Get-T $doc.Paragraphs.Item($i)) -eq '```mermaid') { [void]$found.Add($i) }
}
if ($found.Count -ne 3) { throw "Expected 3 mermaid in 3.4, got $($found.Count)" }

$bi = 2
foreach ($startIdx in ($found | Sort-Object -Descending)) {
    $b = $blocks[$bi]; $bi--
    $endIdx = $startIdx
    for ($j=$startIdx+1; $j -le $doc.Paragraphs.Count; $j++) {
        if ((Get-T $doc.Paragraphs.Item($j)) -eq '```') { $endIdx = $j; break }
    }
    for ($j=$endIdx; $j -ge $startIdx; $j--) { $null = $doc.Paragraphs.Item($j).Range.Delete() }
    $ins = $doc.Paragraphs.Item($startIdx).Range
    $null = $ins.Collapse(0)
    $pic = $ins.InlineShapes.AddPicture($b.Img, $false, $true)
    $pic.LockAspectRatio = -1
    $pic.Width = $word.CentimetersToPoints(10)
    $ins.ParagraphFormat.Alignment = 1
    $ins.ParagraphFormat.FirstLineIndent = 0
    $ins.InsertParagraphAfter() | Out-Null
    $capRng = $doc.Paragraphs.Item($startIdx + 1).Range
    $capRng.Text = "$($b.CapZh)`r$($b.CapEn)`r"
    $capRng.Font.NameFarEast = S 0x5B8B,0x4F53
    $capRng.Font.Name = 'Times New Roman'
    $capRng.Font.Size = 10.5
    $capRng.Font.Bold = -1
    $capRng.ParagraphFormat.Alignment = 1
    Write-Output "Inserted $($b.CapZh)"
}

if (Test-Path $tmpOut) { Remove-Item $tmpOut -Force }
$doc.SaveAs($tmpOut)
$doc.Close($false)
$word.Quit()
[System.Runtime.InteropServices.Marshal]::ReleaseComObject($doc) | Out-Null
[System.Runtime.InteropServices.Marshal]::ReleaseComObject($word) | Out-Null
[GC]::Collect(); Start-Sleep -Seconds 1

$dest1 = Join-Path $baseDir 'thesis_with_flowcharts.docx'
$dest2 = Join-Path $baseDir (($src.BaseName) + '_含流程图.docx')
Copy-Item $tmpOut $dest1 -Force
Copy-Item $tmpOut $dest2 -Force -ErrorAction SilentlyContinue
try { Copy-Item $tmpOut $src.FullName -Force } catch {
    Write-Output "Target locked. Use: $dest1"
}
Write-Output "Done -> $dest1"
