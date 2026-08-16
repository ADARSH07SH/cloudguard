# 🛡️ CloudGuard

**CloudGuard** is a secure, enterprise-grade [Model Context Protocol (MCP)](https://modelcontextprotocol.io/) server built in Java. It provides AI assistants (like Claude, Cursor, and custom LLM agents) with standardized, safe, and governed access to multi-cloud infrastructure operations across **AWS**, **Azure**, and **GCP**.

---

## ✨ Features

- **🌐 Multi-Cloud Management**: Query and manage compute resources across AWS EC2, Azure Virtual Machines, and GCP Compute Engine via a unified interface.
- **🔌 Dual MCP Transports**:
  - **STDIO Transport**: Direct standard I/O stream for desktop AI clients (Claude Desktop, IDE extensions).
  - **HTTP + SSE Transport**: Web server mode with Server-Sent Events (`/mcp/events`) and JSON-RPC message endpoints (`/mcp/message`).
- **🔒 Enterprise Security & Guardrails (Interceptor Chain)**:
  - **JWT Authentication & RBAC**: Verify identity and enforce role permissions.
  - **Approval Workflow**: Require human-in-the-loop approval before executing destructive actions (e.g., terminating instances).
  - **Read-Only Mode**: Safety switch to block write/modify requests.
  - **Audit Logging**: Comprehensive logging of tool invocations for compliance and forensics.
- **📊 Observability & Metrics**: Built-in Prometheus and Micrometer integration for real-time latency, request counts, and error tracking.

---

## 🏗️ Architecture

```
                               ┌──────────────────────────────────────────────────────────┐
                               │                    CloudGuard Server                     │
┌──────────────┐   JSON-RPC    │  ┌────────────┐   ┌───────────────────┐   ┌────────────┐ │
│              │ ────────────> │  │            │   │ Interceptor Chain │   │            │ │   AWS / Azure / GCP
│  MCP Client  │  STDIO / HTTP │  │ Dispatcher │ ─>│ • Security / JWT  │ ─>│    Tool    │ │ ───────────────────>
│ (Claude/IDE) │ <──────────── │  │            │   │ • Approval / RBAC │   │  Registry  │ │      Cloud APIs
└──────────────┘      SSE      │  └────────────┘   │ • Metrics / Audit │   └────────────┘ │
                               │                   └───────────────────┘                  │
                               └──────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

- **Language**: Java 23
- **Build Tool**: Maven
- **Framework**: Spring Web MVC / Jakarta Servlet API
- **Cloud SDK**: AWS SDK for Java v2 (EC2)
- **Security & Tokens**: JJWT (Java JWT), Gson
- **Metrics**: Micrometer (Prometheus registry), SLF4J

---

## 📦 Available MCP Tools

| Tool Name | Description | Requires Approval |
| :--- | :--- | :---: |
| `list_instances` | List compute instances in a specific cloud provider/region | No |
| `list_all_instances` | Aggregated view of instances across AWS, Azure, and GCP | No |
| `delete_instance` | Terminate/delete a specified virtual machine | **Yes** |
| `approve_action` | Approve a pending sensitive action request | No |
| `echo_tool` | Diagnostic echo utility to verify connectivity | No |

---

## 🚀 Getting Started

### Prerequisites

- **Java JDK 23** or higher installed
- **Maven 3.8+**
- Cloud provider credentials configured (e.g., `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`)

### 1. Clone & Build

```bash
git clone https://github.com/ADARSH07SH/cloudguard.git
cd cloudguard
mvn clean package
```

### 2. Run via STDIO (for Claude Desktop / Local MCP Clients)

```bash
java -jar target/cloudguard-1.0-SNAPSHOT.jar
```

#### Claude Desktop Configuration (`claude_desktop_config.json`):
```json
{
  "mcpServers": {
    "cloudguard": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/cloudguard/target/cloudguard-1.0-SNAPSHOT.jar"
      ],
      "env": {
        "AWS_REGION": "us-east-1"
      }
    }
  }
}
```

### 3. Run via HTTP/SSE Server

Start the Spring Web application to expose endpoints:
- **SSE Stream**: `GET http://localhost:8080/mcp/events`
- **JSON-RPC Messages**: `POST http://localhost:8080/mcp/message`
- **Prometheus Metrics**: `GET http://localhost:8080/metrics`

---

## 📄 License

This project is licensed under the Apache 2.0 License.
