# MediWave Services Startup Script

Write-Host "Starting MediWave Healthcare Management System..." -ForegroundColor Green

# Start Kafka and Zookeeper manually (simplified version)
Write-Host "Starting Kafka infrastructure..." -ForegroundColor Yellow

# Check if Java is available
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java available: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "Java not found in PATH" -ForegroundColor Red
    exit 1
}

# Start Zookeeper
Write-Host "Starting Zookeeper..." -ForegroundColor Cyan
Start-Process -FilePath "java" -ArgumentList "-jar", "C:\kafka\bin\windows\zookeeper-server-start.jar", "--config", "C:\kafka\config\zookeeper.properties" -WindowStyle Hidden -ErrorAction SilentlyContinue

# Wait for Zookeeper to start
Start-Sleep -Seconds 5

# Start Kafka
Write-Host "Starting Kafka..." -ForegroundColor Cyan
Start-Process -FilePath "java" -ArgumentList "-jar", "C:\kafka\bin\windows\kafka-server-start.jar", "--config", "C:\kafka\config\server.properties" -WindowStyle Hidden -ErrorAction SilentlyContinue

# Wait for Kafka to start
Start-Sleep -Seconds 10

# Start Spring Boot Services
Write-Host "Starting Spring Boot Services..." -ForegroundColor Yellow

# Start Appointment Service
Write-Host "Starting Appointment Service (Port 8081)..." -ForegroundColor Cyan
Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run", "-Dspring-boot.run.profiles=no-kafka" -WorkingDirectory "appointment-service" -WindowStyle Minimized

# Start Billing Service
Write-Host "Starting Billing Service (Port 8082)..." -ForegroundColor Cyan
Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run", "-Dspring-boot.run.profiles=no-kafka" -WorkingDirectory "billing-service" -WindowStyle Minimized

# Start Resource Management Service
Write-Host "Starting Resource Management Service (Port 8083)..." -ForegroundColor Cyan
Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run", "-Dspring-boot.run.profiles=no-kafka" -WorkingDirectory "resource-management-service" -WindowStyle Minimized

# Start UI Service
Write-Host "Starting Web UI (Port 8084)..." -ForegroundColor Cyan
Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -WorkingDirectory "ui" -WindowStyle Minimized

Write-Host "All services starting up..." -ForegroundColor Green
Write-Host "Access URLs:" -ForegroundColor Yellow
Write-Host "  Web UI: http://localhost:8084" -ForegroundColor White
Write-Host "  Appointment Service: http://localhost:8081" -ForegroundColor White
Write-Host "  Billing Service: http://localhost:8082" -ForegroundColor White
Write-Host "  Resource Management Service: http://localhost:8083" -ForegroundColor White
Write-Host "  H2 Console: http://localhost:8081/h2-console" -ForegroundColor White

# Monitor services
Write-Host "Monitoring services for 30 seconds..." -ForegroundColor Yellow
for ($i = 1; $i -le 6; $i++) {
    Start-Sleep -Seconds 5
    
    $appointment = Test-NetConnection -ComputerName localhost -Port 8081
    $billing = Test-NetConnection -ComputerName localhost -Port 8082
    $resource = Test-NetConnection -ComputerName localhost -Port 8083
    $ui = Test-NetConnection -ComputerName localhost -Port 8084
    
    Write-Host "Check $i: Appointment:$($appointment.TcpTestSucceeded) Billing:$($billing.TcpTestSucceeded) Resource:$($resource.TcpTestSucceeded) UI:$($ui.TcpTestSucceeded)" -ForegroundColor Gray
}

Write-Host "Startup script completed!" -ForegroundColor Green
