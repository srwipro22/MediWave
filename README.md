# MediWave Healthcare Management System

A microservices-based healthcare management system built with Spring Boot, REST APIs, and Apache Kafka for asynchronous communication.

## Architecture Overview

The system consists of three core microservices that communicate asynchronously via Kafka topics:

- **Appointment Service** (Port 8081) - Handles appointment management with REST APIs
- **Billing Service** (Port 8082) - Processes billing triggered by Kafka events
- **Resource Management Service** (Port 8083) - Manages resource allocation via Kafka events

## Kafka Topics

- `appointment-events` - AppointmentBookedEvent messages
- `billing-events` - BillingProcessedEvent messages  
- `resource-events` - ResourceAllocatedEvent messages

## Workflow

1. Patient books appointment via Appointment Service REST API
2. Appointment Service publishes AppointmentBookedEvent to Kafka
3. Billing Service consumes event, processes billing, publishes BillingProcessedEvent
4. Resource Management Service consumes event, allocates resources, publishes ResourceAllocatedEvent
5. Appointment Service consumes billing and resource events to update final appointment status

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### Running the System

1. **Start Kafka Infrastructure:**
   ```bash
   docker-compose up -d
   ```

2. **Build the Project:**
   ```bash
   mvn clean install
   ```

3. **Start the Services:**
   ```bash
   # Terminal 1 - Appointment Service
   cd appointment-service
   mvn spring-boot:run

   # Terminal 2 - Billing Service  
   cd billing-service
   mvn spring-boot:run

   # Terminal 3 - Resource Management Service
   cd resource-management-service
   mvn spring-boot:run

   # Terminal 4 - Web UI
   cd ui
   mvn spring-boot:run
   ```

### Verify Setup

- Kafka UI: http://localhost:8080
- Appointment Service API: http://localhost:8081
- H2 Console (Appointment Service): http://localhost:8081/h2-console
- **Web UI**: http://localhost:8084

## API Endpoints

### Appointment Service

- `POST /appointments` - Book a new appointment
- `GET /appointments/{id}` - Retrieve appointment details
- `PUT /appointments/{id}/status` - Update appointment status

### Sample Appointment Request

```json
{
  "patientId": 123,
  "doctorId": 456,
  "appointmentDate": "2024-01-15T10:30:00",
  "appointmentType": "general",
  "notes": "Regular checkup"
}
```

## Testing

Run the test suite for all services:

```bash
mvn test
```

## Service Details

### Appointment Service
- REST API for appointment CRUD operations
- Publishes AppointmentBookedEvent on appointment creation
- Consumes BillingProcessedEvent and ResourceAllocatedEvent
- Updates appointment status based on processing results

### Billing Service  
- Consumes AppointmentBookedEvent
- Simulates insurance verification and co-pay calculation
- Publishes BillingProcessedEvent (success/failure)
- No direct REST API - event-driven only

### Resource Management Service
- Consumes AppointmentBookedEvent  
- Checks and allocates doctors, rooms, and equipment
- Publishes ResourceAllocatedEvent (success/failure)
- No direct REST API - event-driven only

## Configuration

Each service is configured in `application.yml` with:
- Service-specific ports
- Kafka bootstrap servers
- JSON serialization/deserialization settings
- Logging configuration

## Monitoring

- Kafka UI provides topic and consumer monitoring
- Service logs include detailed event processing information
- H2 console available for Appointment Service database inspection
