# Generate flowchart PNGs and insert into thesis docx (section 3.4)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

function S([int[]]$c) { -join ($c | ForEach-Object { [char]$_ }) }

$baseDir = if ($PSScriptRoot) { $PSScriptRoot } else { (Get-Item 'c:\Users\Administrator\Desktop\*\insert-flowcharts.ps1' -ErrorAction SilentlyContinue | Select-Object -First 1).DirectoryName }
$flowDir = Join-Path $baseDir (([char]0x8BBA)+([char]0x6587)+([char]0x63D2)+([char]0x56FE)+'\flowcharts')
New-Item -ItemType Directory -Force -Path $flowDir | Out-Null

function New-FlowPng {
    param([string]$Path, [int]$W, [int]$H, [array]$Nodes, [array]$Edges, [array]$Branches)
    $bmp = New-Object System.Drawing.Bitmap $W, $H
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.Clear([System.Drawing.Color]::White)
    $pen = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(51,51,51)), 1.2
    $font = New-Object System.Drawing.Font (S 0x5B8B,0x4F53), 10
    $sf = New-Object System.Drawing.StringFormat
    $sf.Alignment = [System.Drawing.StringAlignment]::Center
    $sf.LineAlignment = [System.Drawing.StringAlignment]::Center
    $brush = [System.Drawing.Brushes]::Black
    foreach ($e in $Edges) {
        $g.DrawLine($pen, $e.X1, $e.Y1, $e.X2, $e.Y2)
        if ($e.Lab) { $g.DrawString($e.Lab, (New-Object System.Drawing.Font (S 0x5B8B,0x4F53), 9), $brush, ($e.X1+$e.X2)/2, ($e.Y1+$e.Y2)/2-12, $sf) }
    }
    foreach ($b in $Branches) {
        $g.DrawLine($pen, $b.X1, $b.Y1, $b.X2, $b.Y2)
        if ($b.Lab) { $g.DrawString($b.Lab, (New-Object System.Drawing.Font (S 0x5B8B,0x4F53), 9), $brush, ($b.X1+$b.X2)/2, ($b.Y1+$b.Y2)/2-10, $sf) }
    }
    foreach ($n in $Nodes) {
        if ($n.Diamond) {
            $pts = @(
                [System.Drawing.Point]::new([int]($n.X+$n.W/2), $n.Y),
                [System.Drawing.Point]::new($n.X+$n.W, [int]($n.Y+$n.H/2)),
                [System.Drawing.Point]::new([int]($n.X+$n.W/2), $n.Y+$n.H),
                [System.Drawing.Point]::new($n.X, [int]($n.Y+$n.H/2))
            )
            $g.DrawPolygon($pen, $pts)
        } else { $g.DrawRectangle($pen, $n.X, $n.Y, $n.W, $n.H) }
        $rect = New-Object System.Drawing.RectangleF ($n.X+4), ($n.Y+4), ($n.W-8), ($n.H-8)
        $g.DrawString($n.Text, $font, $brush, $rect, $sf)
    }
    $bmp.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose(); $bmp.Dispose(); $pen.Dispose(); $font.Dispose()
}

$bw=200;$bh=34;$cx=160
$tBrowse = S 0x7528,0x6237,0x6D4F,0x89C8,0x5BA0,0x7269,0x5217,0x8868
$tDetail = S 0x67E5,0x770B,0x5BA0,0x7269,0x8BE6,0x60C5
$tRules  = S 0x9605,0x8BFB,0x9886,0x517B,0x987B,0x77E5
$tLogin  = S 0x767B,0x5F55,0x7CFB,0x7EDF
$tApply  = S 0x586B,0x5199,0x9886,0x517B,0x7533,0x8BF7
$tAudit  = S 0x540E,0x53F0,0x5BA1,0x6838
$tResult = S 0x5BA1,0x6838,0x7ED3,0x679C
$tPass   = S 0x901A,0x8FC7
$tReject = S 0x62D2,0x7EDD
$tFollow = S 0x8FDB,0x5165,0x56DE,0x8BBF,0x4E0E,0x9886,0x517B,0x8DDF,0x8FDB
$tRemark = S 0x8FD4,0x56DE,0x7ED3,0x679C,0x5E76,0x8BB0,0x5F55,0x5907,0x6CE8
$tNeed   = S 0x7528,0x6237,0x67E5,0x770B,0x6025,0x9700,0x7269,0x8D44,0x6E05,0x5355
$tClaim  = S 0x63D0,0x4EA4,0x8BA4,0x9886,0x4FE1,0x606F
$tHandover = S 0x8054,0x7CFB,0x4EA4,0x63A5,0x7269,0x8D44
$tEnd    = S 0x8BB0,0x5F55,0x539F,0x56E0,0x5E76,0x7ED3,0x675F
$tPost   = S 0x53D1,0x5E03,0x5E16,0x5B50
$tUpload = S 0x4E0A,0x4F20,0x56FE,0x7247,0x4E0E,0x5B9A,0x4F4D
$tComment = S 0x5176,0x4ED6,0x7528,0x6237,0x8BC4,0x8BBA,0x56DE,0x590D
$tMod    = S 0x7BA1,0x7406,0x5458,0x5BA1,0x6838,0x2F,0x5904,0x7406,0x5F02,0x5E38,0x5185,0x5BB9

$nodes1 = @(
    @{X=$cx;Y=15;W=$bw;H=$bh;Text=$tBrowse}
    @{X=$cx;Y=65;W=$bw;H=$bh;Text=$tDetail}
    @{X=$cx;Y=115;W=$bw;H=$bh;Text=$tRules}
    @{X=$cx;Y=165;W=$bw;H=$bh;Text=$tLogin}
    @{X=$cx;Y=215;W=$bw;H=$bh;Text=$tApply}
    @{X=$cx;Y=265;W=$bw;H=$bh;Text=$tAudit}
    @{X=$cx;Y=320;W=$bw;H=$bh;Text=$tResult;Diamond=$true}
    @{X=40;Y=400;W=220;H=$bh;Text=$tFollow}
    @{X=280;Y=400;W=220;H=$bh;Text=$tRemark}
)
$edges1 = @(); for ($i=0;$i -lt 5;$i++){ $y1=15+$i*50+$bh; $y2=65+$i*50; $edges1+=@{X1=260;Y1=$y1;X2=260;Y2=$y2} }
$edges1 += @(@{X1=260;Y1=299;X2=260;Y2=320},@{X1=260;Y1=354;X2=260;Y2=400})
$br1 = @(@{X1=220;Y1=385;X2=150;Y2=400;Lab=$tPass},@{X1=300;Y1=385;X2=390;Y2=400;Lab=$tReject})
$p1 = Join-Path $flowDir 'fig3-1-adoption.png'
New-FlowPng $p1 520 480 $nodes1 $edges1 $br1

$nodes2 = @(
    @{X=130;Y=15;W=260;H=$bh;Text=$tNeed}
    @{X=$cx;Y=75;W=$bw;H=$bh;Text=$tLogin}
    @{X=$cx;Y=135;W=$bw;H=$bh;Text=$tClaim}
    @{X=$cx;Y=195;W=$bw;H=$bh;Text=$tAudit}
    @{X=$cx;Y=255;W=$bw;H=$bh;Text=$tResult;Diamond=$true}
    @{X=40;Y=335;W=200;H=$bh;Text=$tHandover}
    @{X=280;Y=335;W=200;H=$bh;Text=$tEnd}
)
$edges2 = @(@{X1=260;Y1=49;X2=260;Y2=75},@{X1=260;Y1=109;X2=260;Y2=135},@{X1=260;Y1=169;X2=260;Y2=195},@{X1=260;Y1=229;X2=260;Y2=255},@{X1=260;Y1=289;X2=260;Y2=335})
$br2 = @(@{X1=220;Y1=310;X2=140;Y2=335;Lab=$tPass},@{X1=300;Y1=310;X2=380;Y2=335;Lab=$tReject})
$p2 = Join-Path $flowDir 'fig3-2-donation.png'
New-FlowPng $p2 520 420 $nodes2 $edges2 $br2

$nodes3 = @(
    @{X=$cx;Y=15;W=$bw;H=$bh;Text=$tLogin}
    @{X=$cx;Y=75;W=$bw;H=$bh;Text=$tPost}
    @{X=$cx;Y=135;W=$bw;H=$bh;Text=$tUpload}
    @{X=130;Y=195;W=260;H=$bh;Text=$tComment}
    @{X=90;Y=255;W=340;H=$bh;Text=$tMod}
)
$edges3 = @(@{X1=260;Y1=49;X2=260;Y2=75},@{X1=260;Y1=109;X2=260;Y2=135},@{X1=260;Y1=169;X2=260;Y2=195},@{X1=260;Y1=229;X2=260;Y2=255})
$p3 = Join-Path $flowDir 'fig3-3-community.png'
New-FlowPng $p3 520 340 $nodes3 $edges3 @()

Write-Output "PNG OK"

Get-Process WINWORD -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

$docFile = Get-ChildItem $baseDir -Filter '*.docx' | Where-Object {
    $_.Name -notlike '~$*' -and $_.Name -notlike 'thesis_work*' -and $_.BaseName.EndsWith('2')
} | Select-Object -First 1
if (-not $docFile) {
    $docFile = Get-ChildItem $baseDir -Filter '*.docx' | Where-Object {
        $_.Name -notlike '~$*' -and $_.Length -gt 300000 -and $_.Length -lt 315000
    } | Sort-Object LastWriteTime -Descending | Select-Object -First 1
}
if (-not $docFile) { throw 'doc not found' }
Write-Output "Target: $($docFile.Name)"

$cap1zh = (S 0x56FE) + '3-1  ' + (S 0x9886,0x517B,0x4E1A,0x52A1,0x6D41,0x7A0B,0x56FE)
$cap2zh = (S 0x56FE) + '3-2  ' + (S 0x7269,0x8D44,0x8BA4,0x9886,0x6D41,0x7A0B,0x56FE)
$cap3zh = (S 0x56FE) + '3-3  ' + (S 0x793E,0x533A,0x4E92,0x52A8,0x6D41,0x7A0B,0x56FE)
$blocks = @(
    @{ Img=$p1; CapZh=$cap1zh; CapEn='Figure 3-1  Adoption Business Process' }
    @{ Img=$p2; CapZh=$cap2zh; CapEn='Figure 3-2  Donation Claim Process' }
    @{ Img=$p3; CapZh=$cap3zh; CapEn='Figure 3-3  Community Interaction Process' }
)

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$word.DisplayAlerts = 0
$doc = $word.Documents.Open($docFile.FullName)

function Get-T($p) { ($p.Range.Text -replace "[\r\n\u0007]", '').Trim() }

$found = [System.Collections.Generic.List[int]]::new()
$pStart = 0; $pEnd = $doc.Paragraphs.Count
for ($i = 1; $i -le $doc.Paragraphs.Count; $i++) {
    $t = Get-T $doc.Paragraphs.Item($i)
    if ($t -match '^3\.4\.1') { $pStart = $i }
    if ($t -match '^3\.5') { $pEnd = $i; break }
}
for ($i = $pStart; $i -lt $pEnd; $i++) {
    if ((Get-T $doc.Paragraphs.Item($i)) -eq '```mermaid') { [void]$found.Add($i) }
}
if ($found.Count -lt 3) { throw "section 3.4 mermaid blocks: $($found.Count) (range $pStart-$pEnd)" }
if ($found.Count -gt 3) { $found = [System.Collections.Generic.List[int]]($found | Select-Object -First 3) }

$bi = 2
foreach ($startIdx in ($found | Sort-Object -Descending)) {
    $b = $blocks[$bi]; $bi--
    $endIdx = $startIdx
    for ($j = $startIdx + 1; $j -le $doc.Paragraphs.Count; $j++) {
        if ((Get-T $doc.Paragraphs.Item($j)) -eq '```') { $endIdx = $j; break }
    }
    for ($j = $endIdx; $j -ge $startIdx; $j--) {
        $null = $doc.Paragraphs.Item($j).Range.Delete()
    }
    $ins = $doc.Paragraphs.Item($startIdx).Range
    $null = $ins.Collapse(0)
    $pic = $ins.InlineShapes.AddPicture($b.Img, $false, $true)
    $pic.LockAspectRatio = -1
    $pic.Width = $word.CentimetersToPoints(10)
    $ins.ParagraphFormat.Alignment = 1
    $ins.ParagraphFormat.FirstLineIndent = 0
    $ins.ParagraphFormat.CharacterUnitFirstLineIndent = 0
    $ins.InsertParagraphAfter()
    $capRng = $doc.Paragraphs.Item($startIdx + 1).Range
    $capRng.Text = "$($b.CapZh)`r$($b.CapEn)`r"
    $capRng.Font.NameFarEast = S 0x5B8B,0x4F53
    $capRng.Font.Name = 'Times New Roman'
    $capRng.Font.Size = 10.5
    $capRng.Font.Bold = -1
    $capRng.ParagraphFormat.Alignment = 1
    $capRng.ParagraphFormat.FirstLineIndent = 0
    Write-Output "OK $($b.CapZh)"
}

$doc.Save()
$doc.Close($false)
$word.Quit()
Write-Output "Saved: $($docFile.FullName)"
