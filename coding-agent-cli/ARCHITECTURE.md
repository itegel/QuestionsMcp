# Coding Agent CLI 架构文档

## 项目概述

Coding Agent CLI 是一个智能编码助手，通过自然语言对话完成代码分析、生成、审查等任务。采用 ReAct（Reasoning + Acting）架构，实现智能推理和工具使用能力。

## 核心架构

### 1. ReAct 框架（核心引擎）

ReAct 框架实现了 **思考 - 行动 - 观察** 的循环推理模式：

```
用户请求 → 思考 → 行动 → 观察 → 反思 → 思考 → ... → 最终答案
```

#### 核心组件：

**1.1 ReActAgent** (`src/main/java/com/codingagent/agent/react/ReActAgent.java`)
- 主控制器，管理整个 ReAct 循环
- 维护状态机（ReActState）
- 协调 LLM 推理和工具执行
- 支持最大迭代次数限制

**1.2 ThoughtParser** (`src/main/java/com/codingagent/agent/react/ThoughtParser.java`)
- 解析 LLM 输出，识别思考、行动、观察和最终答案
- 支持中英文混合输入
- 提取行动参数

**1.3 ActionOrchestrator** (`src/main/java/com/codingagent/agent/react/ActionOrchestrator.java`)
- 执行工具调用
- 处理工具返回结果
- 提供工具描述和可用性检查

**1.4 状态管理**
- `Thought`: 思考记录（类型：REASONING, ACTION, OBSERVATION, FINAL_ANSWER）
- `Action`: 行动指令（工具名称 + 参数）
- `ReActState`: 完整状态管理（会话、任务、历史、迭代计数）

### 2. 智能决策系统

#### 2.1 IntentRecognizer（意图识别）
- 使用 LLM 分析用户输入
- 识别 9 种意图类型：
  - ANALYZE: 分析代码
  - GENERATE: 生成代码
  - REVIEW: 审查代码
  - REFACTOR: 重构代码
  - DEBUG: 调试 bug
  - EXPLAIN: 解释代码
  - TEST: 测试代码
  - SEARCH: 搜索文件
  - CHAT: 一般对话
- 提取任务参数（文件路径、语言等）
- 提供置信度评估

#### 2.2 TaskPlanner（任务规划）
- 将复杂任务拆解为子任务
- 生成可执行的任务列表
- 支持任务类型：read_file, write_file, analyze, search, execute, review, generate, test

#### 2.3 AgentRouter（Agent 路由）
- 根据意图自动选择合适的 Agent
- 支持的 Agent：
  - CodeAnalyzerAgent: 代码分析
  - CodeGeneratorAgent: 代码生成
  - CodeReviewerAgent: 代码审查
  - ReActAgent: 智能推理（默认）

### 3. 工具系统

#### 3.1 工具接口
```java
public interface Tool {
    String getName();
    String getDescription();
    String execute(String[] args);
    Map<String, Object> executeWithMap(Map<String, Object> parameters);
    String getSchema();
}
```

#### 3.2 内置工具

**FileTool** - 文件操作
- read: 读取文件
- write: 写入文件
- list: 列出目录

**CommandTool** - 命令执行
- 执行系统命令
- 支持超时控制

**SearchTool** - 文件搜索（新增）
- 按文件名搜索
- 按扩展名搜索
- 支持通配符

**DiffTool** - 文件对比（新增）
- 比较两个文件的差异
- 逐行显示不同

**GitTool** - Git 操作（新增）
- status, log, diff 等命令
- 支持指定仓库路径

#### 3.3 ToolManager
- 工具注册和发现
- 工具执行协调
- 提供工具描述

### 4. 记忆系统

#### 4.1 MemoryService
- 管理会话记忆
- 存储对话历史
- 提供上下文消息

#### 4.2 Memory 接口
- 定义记忆管理方法
- 支持多种实现

#### 4.3 InMemory 实现
- 基于内存的记忆存储
- 快速访问
- 会话隔离

### 5. Agent 系统

#### 5.1 BaseAgent（基础类）
- 提供通用属性：name, role
- 注入依赖：QwenClient, ToolManager, MemoryService
- 定义抽象方法：process()

#### 5.2 专用 Agent

**CodeAnalyzerAgent**
- 分析代码结构
- 识别功能和问题

**CodeGeneratorAgent**
- 根据需求生成代码
- 遵循最佳实践

**CodeReviewerAgent**
- 审查代码质量
- 检查安全性

**ReActAgent**（智能推理 Agent）
- 支持复杂任务
- 自主使用工具
- 多步推理

### 6. CLI 接口

#### 6.1 ChatCommand（统一入口）
- 自然语言对话界面
- 意图识别展示
- Agent 选择透明化
- 支持命令：
  - help: 显示帮助
  - tools: 显示可用工具
  - exit: 退出

#### 6.2 其他命令（向后兼容）
- analyze: 直接分析
- generate: 直接生成
- review: 直接审查
- file: 文件操作
- command: 命令执行
- config: 配置管理

## 工作流程示例

### 示例 1：代码分析
```
用户：帮我分析 src/main/java/App.java 的代码结构

1. IntentRecognizer 识别意图：ANALYZE（高置信度）
2. AgentRouter 选择 CodeAnalyzerAgent
3. CodeAnalyzerAgent 调用 LLM 分析代码
4. 返回分析结果
```

### 示例 2：复杂任务（ReAct 模式）
```
用户：重构这个模块，提高代码质量

1. IntentRecognizer 识别意图：REFACTOR
2. AgentRouter 选择 ReActAgent（智能推理模式）
3. ReAct 循环开始：
   - 思考：需要先查看代码
   - 行动：file | action=read, path=src/main/java/Module.java
   - 观察：[代码内容]
   - 思考：识别到代码质量问题
   - 行动：file | action=write, path=..., content=[改进后的代码]
   - 观察：文件已更新
   - 思考：任务完成
   - 最终答案：已完成重构，改进了...
```

### 示例 3：调试任务
```
用户：查找并修复这个 bug

1. IntentRecognizer 识别意图：DEBUG
2. AgentRouter 选择 ReActAgent
3. ReAct 循环：
   - 思考：需要查看错误信息和相关代码
   - 行动：file | action=read, path=...
   - 观察：[代码内容]
   - 思考：分析可能的 bug 原因
   - 行动：command | command=git log --oneline -10
   - 观察：[提交历史]
   - 思考：定位到问题
   - 行动：file | action=write, path=..., content=[修复后的代码]
   - 最终答案：发现并修复了...
```

## 技术栈

- **语言**: Java 8
- **LLM 客户端**: OkHttp + Jackson
- **CLI 框架**: Picocli
- **构建工具**: Maven
- **API**: 阿里云 Dashscope（Qwen）

## 目录结构

```
coding-agent-cli/
├── src/main/java/com/codingagent/
│   ├── agent/
│   │   ├── base/
│   │   │   └── BaseAgent.java
│   │   ├── code/
│   │   │   ├── CodeAnalyzerAgent.java
│   │   │   ├── CodeGeneratorAgent.java
│   │   │   └── CodeReviewerAgent.java
│   │   ├── react/           # ReAct 框架
│   │   │   ├── ReActAgent.java
│   │   │   ├── Thought.java
│   │   │   ├── ThoughtParser.java
│   │   │   ├── Action.java
│   │   │   ├── ReActState.java
│   │   │   └── ActionOrchestrator.java
│   │   └── decision/        # 智能决策
│   │       ├── IntentRecognizer.java
│   │       ├── TaskPlanner.java
│   │       └── AgentRouter.java
│   ├── tool/
│   │   ├── Tool.java
│   │   ├── ToolManager.java
│   │   ├── FileTool.java
│   │   ├── CommandTool.java
│   │   ├── SearchTool.java
│   │   ├── DiffTool.java
│   │   └── GitTool.java
│   ├── memory/
│   │   ├── Memory.java
│   │   ├── InMemory.java
│   │   └── MemoryService.java
│   ├── cli/
│   │   ├── Main.java
│   │   └── command/
│   │       ├── ChatCommand.java
│   │       ├── AnalyzeCommand.java
│   │       ├── GenerateCommand.java
│   │       ├── ReviewCommand.java
│   │       ├── FileCommand.java
│   │       ├── CommandCommand.java
│   │       └── ConfigCommand.java
│   └── util/
│       ├── QwenClient.java
│       ├── QwenRequest.java
│       ├── QwenResponse.java
│       └── ConfigLoader.java
└── pom.xml
```

## 关键设计决策

### 1. 为什么选择 ReAct 架构？
- **透明性**: 思考过程可见，易于调试
- **灵活性**: 可以处理多种任务类型
- **可扩展**: 容易添加新工具和能力
- **自我修正**: 通过观察结果调整策略

### 2. 意图识别 vs 直接路由
- 使用 LLM 进行意图识别更灵活
- 可以处理模糊和复杂的用户输入
- 提供置信度评估，低置信度时可以询问用户

### 3. 工具描述标准化
- 每个工具提供 schema 描述
- 支持 LLM 理解工具参数
- 便于工具发现和自动使用

### 4. 会话记忆
- 保持对话上下文
- 支持多轮对话
- 会话隔离，避免干扰

## 性能优化

### 1. 迭代次数限制
- 默认最大 10 次迭代
- 防止无限循环
- 可配置

### 2. 上下文窗口管理
- 只保留相关历史
- 避免 token 超限
- 智能选择上下文

### 3. 工具执行优化
- 并行执行独立工具
- 缓存工具结果
- 超时控制

## 安全考虑

### 1. 命令执行沙箱
- 限制可执行命令
- 超时保护
- 输出长度限制

### 2. 文件操作权限
- 限制访问目录
- 只读模式选项
- 敏感文件保护

### 3. API 密钥管理
- 配置文件存储
- 不记录敏感信息
- 支持环境变量

## 扩展指南

### 添加新工具
1. 实现 `Tool` 接口
2. 在 ToolManager 中注册
3. 提供清晰的描述和 schema

### 添加新 Agent
1. 继承 `BaseAgent`
2. 实现 `process()` 方法
3. 在 AgentRouter 中注册

### 自定义意图类型
1. 在 IntentRecognizer.IntentType 中添加
2. 更新意图识别 prompt
3. 在 AgentRouter 中添加路由逻辑

## 未来规划

### 短期（已完成）
- ✅ ReAct 框架实现
- ✅ 意图识别和任务规划
- ✅ 工具系统扩展
- ✅ 统一对话入口

### 中期（计划中）
- [ ] 自我反思机制（ReflectionEngine）
- [ ] 多 Agent 协作
- [ ] 向量数据库集成（长期记忆）
- [ ] 代码库语义索引

### 长期（愿景）
- [ ] 插件系统
- [ ] Web 界面
- [ ] 团队协作功能
- [ ] AI 模型抽象层（支持多种 LLM）

## 总结

通过引入 ReAct 架构和智能决策系统，Coding Agent CLI 已经从简单的 API 包装器升级为真正的智能编码助手。核心改进包括：

1. **自主决策**: Agent 可以根据任务自主选择工具和策略
2. **透明推理**: 思考过程可见，易于理解和调试
3. **灵活扩展**: 模块化设计，易于添加新工具和能力
4. **自然交互**: 统一的对话入口，无需记忆命令

这为后续实现更高级的 AI 功能（如自我反思、多 Agent 协作等）奠定了坚实基础。
