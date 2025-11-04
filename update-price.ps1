param(
    [Parameter(Mandatory=$true)]
    [string]$itemId,
    [Parameter(Mandatory=$true)]
    [decimal]$price
)

$body = @{
    itemId = $itemId
    price = $price
} | ConvertTo-Json

Write-Host "Actualizando precio del item $itemId a $price USD..."

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/items/$itemId/price" -Method PUT -Body $body -ContentType "application/json"
    Write-Host "¡Precio actualizado correctamente!" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json) -ForegroundColor Cyan
} catch {
    Write-Host "Error al actualizar el precio: $_" -ForegroundColor Red
    exit 1
}