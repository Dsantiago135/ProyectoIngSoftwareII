<#
.SYNOPSIS
Sirve la interfaz estática en localhost:5500 para desarrollo.
.DESCRIPTION
Entrega HTML, CSS y módulos JavaScript sin instalar dependencias. Detener con Ctrl+C.
#>

$ErrorActionPreference = 'Stop'
$root = [System.IO.Path]::GetFullPath($PSScriptRoot)
$rootPrefix = $root.TrimEnd([System.IO.Path]::DirectorySeparatorChar) + [System.IO.Path]::DirectorySeparatorChar
$listener = [System.Net.HttpListener]::new()
$listener.Prefixes.Add('http://localhost:5500/')

$contentTypes = @{
    '.html' = 'text/html; charset=utf-8'
    '.css'  = 'text/css; charset=utf-8'
    '.js'   = 'text/javascript; charset=utf-8'
    '.svg'  = 'image/svg+xml'
}

try {
    $listener.Start()
    Write-Output 'Interfaz Saber Pro disponible en http://localhost:5500/'
    while ($listener.IsListening) {
        $context = $listener.GetContext()
        try {
            $relativePath = [System.Uri]::UnescapeDataString($context.Request.Url.AbsolutePath.TrimStart('/'))
            if ([string]::IsNullOrWhiteSpace($relativePath)) { $relativePath = 'index.html' }
            $filePath = [System.IO.Path]::GetFullPath((Join-Path $root $relativePath.Replace('/', [System.IO.Path]::DirectorySeparatorChar)))
            $response = $context.Response
            $response.Headers['Cache-Control'] = 'no-store'

            if (-not $filePath.StartsWith($rootPrefix, [System.StringComparison]::OrdinalIgnoreCase) -or
                -not (Test-Path -LiteralPath $filePath -PathType Leaf)) {
                $response.StatusCode = 404
                $bytes = [System.Text.Encoding]::UTF8.GetBytes('No encontrado')
                $response.ContentType = 'text/plain; charset=utf-8'
                $response.ContentLength64 = $bytes.Length
                if ($context.Request.HttpMethod -ne 'HEAD') { $response.OutputStream.Write($bytes, 0, $bytes.Length) }
            }
            else {
                $bytes = [System.IO.File]::ReadAllBytes($filePath)
                $extension = [System.IO.Path]::GetExtension($filePath)
                if ($contentTypes.ContainsKey($extension)) { $response.ContentType = $contentTypes[$extension] }
                $response.ContentLength64 = $bytes.Length
                if ($context.Request.HttpMethod -ne 'HEAD') { $response.OutputStream.Write($bytes, 0, $bytes.Length) }
            }
            $response.Close()
        }
        catch {
            $context.Response.StatusCode = 500
            $context.Response.Close()
        }
    }
}
finally {
    if ($listener.IsListening) { $listener.Stop() }
    $listener.Close()
}