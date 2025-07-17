# SIGMS - Sistema Inteligente de Gestão e Monitorização de Salas

## Overview
SIGMS is an intelligent system designed to manage and monitor classroom spaces in academic environments. It provides real-time information about room availability, incident reporting, and schedule changes through a mobile/web application. The system also includes a hardware component using Raspberry Pi to estimate room occupancy via Wi-Fi probe requests.

## Key Features
- **Real-time room status monitoring** (available, occupied, under maintenance)
- **Incident reporting system** for technical issues (projectors, power outlets, etc.)
- **Schedule change notifications** for students and faculty
- **Occupancy estimation** using Wi-Fi probe requests (IEEE 802.11 protocol)
- **Multi-platform access** (web, iOS, Android)
- **Role-based access control** (students, professors, maintenance staff, administrators)
- **Centralized communication platform**

## System Architecture
The system consists of three main components:

### 1. Frontend (Mobile/Web Application)
- Built with React Native, TypeScript, and Expo
- Cross-platform compatibility (iOS, Android, Web)
- Modular component-based architecture

### 2. Backend
- Kotlin with Ktor framework
- PostgreSQL database with Ktorm ORM
- RESTful API with WebSocket support for real-time updates
- Hybrid authentication (JWT + OAuth 2.0 with Microsoft Entra ID)

### 3. Embedded System
- Raspberry Pi with custom Go application
- Wi-Fi packet capture and processing (tshark)
- Occupancy estimation using EWMA (Exponential Weighted Moving Average)
- WebSocket communication with backend

## Technical Specifications
| Component          | Technology/Standard               |
|--------------------|-----------------------------------|
| Authentication     | OAuth 2.0 + JWT                   |
| Database           | PostgreSQL                        |
| Real-time Comm     | WebSockets                        |
| Security           | HTTPS, CSRF protection, CORS      |
| Deployment         | Kubernetes on GCP                 |

## Installation & Deployment
```bash
# Backend
- JDK 23 (OpenJDK)
- Gradle build
- Containerized with Docker (multi-architecture)

# Frontend
- Node.js 22
- Nginx server with SSL/TLS
- Automated deployment via GitHub Actions

# Database
- PostgreSQL container with persistent volume
- Automatic maintenance tasks

## Usage Guide

### Students
- View class schedules  
- Check room availability  
- Report classroom issues  
- Monitor study room occupancy  

### Professors
- Manage class schedules  
- Announce schedule changes  
- Access office hours information  

### Maintenance Staff
- Receive and resolve incident reports  
- Monitor room conditions  

### Administrators
- Manage user roles and approvals  
- Configure system settings  
- Access usage statistics  

## Future Work
- [ ] Enhanced occupancy estimation with Bluetooth  
- [ ] Offline mode for mobile app  
- [ ] Integration with calendar systems (Google Calendar, Outlook)  
- [ ] Advanced analytics dashboard  
- [ ] Multi-language support  
- [ ] CA-signed SSL certificates  

## Contributors
| Role               | Name             |
|--------------------|------------------|
| Developer          | Tomás Martinho   |
| Developer          | Felipe Alvarez   |
| Developer          | Nuno Aguiar      |
| Project Advisor    | Nuno Leite       |

##Institution: Instituto Superior de Engenharia de Lisboa  
##Date: July 2025  
