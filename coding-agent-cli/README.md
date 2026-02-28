# Coding Agent CLI

基于 Java 和 Qwen 大模型的 Coding Agent 命令行工具，支持代码分析、代码生成、代码审查、文件操作和命令执行等功能。

## 技术栈

- **编程语言**：Java 1.8
- **LLM**：阿里 Qwen
- **CLI 框架**：Picocli
- **HTTP 客户端**：OkHttp
- **配置管理**：Properties 文件

## 功能特性

- **基础对话**：与 Qwen 大模型进行基础对话
- **代码分析**：分析代码结构、功能和潜在问题
- **代码生成**：根据需求生成代码
- **代码审查**：审查代码质量、安全性、可读性和性能
- **文件操作**：读取、写入、列出文件
- **命令执行**：执行系统命令
- **记忆管理**：维护会话上下文
- **多 Agent 架构**：支持不同角色的 Agent 协作

## 项目结构

```
coding-agent-cli/
├── src/main/java/
│   ├── com/codingagent/
│   │   ├── cli/            # CLI 相关
│   │   │   ├── command/     # 命令定义
│   │   │   └── Main.java    # 主入口
│   │   ├── agent/           # Agent 核心
│   │   │   ├── base/        # 基础 Agent
│   │   │   ├── code/        # 代码相关 Agent
│   │   │   └── manager/     # Agent 管理器
│   │   ├── service/         # 服务层
│   │   ├── tool/            # 工具类
│   │   ├── memory/          # 记忆管理
│   │   ├── prompt/          # Prompt 模板
│   │   └── util/            # 工具方法
│   └── resources/           # 资源文件
│       └── config.properties # 配置文件
├── src/test/                # 测试文件
├── pom.xml                  # Maven 配置
└── README.md                # 项目说明
```

## 快速开始

### 1. 配置 Qwen API 密钥

在 `src/main/resources/config.properties` 文件中配置 Qwen API 密钥：

```properties
# Qwen API 配置
qwen.api.url=https://ark.cn-beijing.volces.com/api/v3/chat/completions
qwen.api.api-key=YOUR_API_KEY  # 替换为你的 Qwen API 密钥
qwen.api.model=qwen-max
qwen.timeout=60000
```

### 2. 构建项目

```bash
mvn clean package
```

### 3. 运行项目

```bash
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar
```

## 命令使用

### 1. 基础对话

```bash
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar chat
```

### 2. 代码分析

```bash
# 分析文件
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar analyze --file path/to/code.java

# 分析代码片段
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar analyze --code "public class Test {}"
```

### 3. 代码生成

```bash
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar generate --requirements "生成一个 Java 方法，计算斐波那契数列"
```

### 4. 代码审查

```bash
# 审查文件
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar review --file path/to/code.java

# 审查代码片段
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar review --code "public class Test {}"
```

### 5. 文件操作

```bash
# 读取文件
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar file read test.txt

# 写入文件
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar file write test.txt "Hello, Coding Agent!"

# 列出文件
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar file list .
```

### 6. 命令执行

```bash
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar command ls -la
```

### 7. 配置管理

```bash
# 列出配置
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar config --list

# 设置配置
java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar config --set qwen.api.api-key=YOUR_API_KEY
```

## 配置说明

在 `config.properties` 文件中可以配置以下参数：

- **Qwen API 配置**：API URL、API 密钥、模型名称、超时时间
- **工具配置**：文件操作基础路径、命令执行开关
- **记忆配置**：最大上下文大小、存储类型
- **日志配置**：日志级别

## 注意事项

1. **安全风险**：命令执行功能可能存在安全风险，请谨慎使用
2. **API 费用**：使用 Qwen API 会产生费用，请合理使用
3. **性能优化**：对于大型代码分析，可能需要调整超时时间
4. **错误处理**：系统会捕获并返回错误信息，但仍需合理处理异常情况

## 扩展建议

1. **添加更多 Agent**：如测试生成 Agent、文档生成 Agent 等
2. **优化记忆管理**：使用 Redis 等外部存储提高记忆容量
3. **添加用户认证**：保护接口安全
4. **集成 CI/CD**：自动构建和部署
5. **添加监控**：监控系统性能和使用情况

## 许可证

MIT License
