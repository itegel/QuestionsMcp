package com.codingagent.cli.command;

import com.codingagent.agent.decision.AgentRouter;
import com.codingagent.agent.decision.IntentRecognizer;
import com.codingagent.agent.decision.TaskPlanner;
import com.codingagent.agent.decision.IntentRecognizer.Intent;
import com.codingagent.memory.MemoryService;
import com.codingagent.tool.ToolManager;
import picocli.CommandLine;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

@CommandLine.Command(name = "chat", description = "与智能编码助手对话")
public class ChatCommand implements Runnable {

    @CommandLine.Option(names = {"-s", "--session"}, defaultValue = "default", description = "会话 ID")
    private String sessionId;

    @Override
    public void run() {
        ToolManager toolManager = new ToolManager();
        MemoryService memoryService = new MemoryService();
        IntentRecognizer intentRecognizer = new IntentRecognizer();
        TaskPlanner taskPlanner = new TaskPlanner();
        AgentRouter agentRouter = new AgentRouter(toolManager);
        com.codingagent.agent.collaboration.MultiAgentCoordinator coordinator = 
            new com.codingagent.agent.collaboration.MultiAgentCoordinator(toolManager);

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     智能编码助手 - Intelligent Coding Agent    ║");
        System.out.println("║  支持：分析、生成、审查、重构、调试等能力     ║");
        System.out.println("║  新增：自我反思、多 Agent 协作                ║");
        System.out.println("║  输入 'help' 查看帮助，'exit' 退出           ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();

        try {
            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();

            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new StringsCompleter("help", "tools", "agents", "collaborate", "exit", "quit"))
                    .build();

            while (true) {
                String input;
                try {
                    input = lineReader.readLine("👤 你：");
                } catch (org.jline.reader.UserInterruptException | org.jline.reader.EndOfFileException e) {
                    break;
                }

                if (input == null || input.trim().isEmpty()) {
                    continue;
                }
                
                input = input.trim();

                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                    break;
                }

                if (input.equalsIgnoreCase("help")) {
                    showHelp();
                    continue;
                }

                if (input.equalsIgnoreCase("tools")) {
                    showTools(toolManager);
                    continue;
                }

                if (input.equalsIgnoreCase("agents")) {
                    coordinator.showAgentStatus();
                    continue;
                }

                try {
                    Intent intent = intentRecognizer.recognize(input);
                    
                    System.out.println("\n🤖 意图识别:");
                    System.out.println("   类型：" + intent.getType().getName());
                    System.out.println("   置信度：" + intent.getConfidence());
                    System.out.println("   理由：" + intent.getReasoning());
                    System.out.println();

                    String response;
                    // 自动判断是否需要协作：显式请求、识别为复杂任务、或置信度低但任务描述长
                    boolean shouldCollaborate = input.toLowerCase().contains("collaborate") || 
                                               "true".equals(intent.getParameters().get("complex")) ||
                                               (intent.getConfidence().equals("低") && input.length() > 50);

                    if (shouldCollaborate) {
                        System.out.println("🔄 检测到复杂任务，启动多 Agent 协作模式...");
                        response = coordinator.coordinate(sessionId, input);
                    } else {
                        com.codingagent.agent.base.BaseAgent agent = agentRouter.selectAgent(intent);
                        System.out.println("🤖 已选择 " + agent.getName() + " 来处理你的请求");
                        
                        if (agent instanceof com.codingagent.agent.react.ReActAgent) {
                            System.out.println("🔄 启动智能推理模式 (ReAct)...");
                        }
                        response = agent.process(sessionId, input);
                    }

                    System.out.println("\n🤖 助手：" + response);
                    System.out.println("═══════════════════════════════════════════\n");

                } catch (Exception e) {
                    System.out.println("❌ 处理请求时出错：" + (e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
                    System.out.println("错误类型：" + e.getClass().getName());
                    System.out.println("\n详细堆栈跟踪:");
                    e.printStackTrace(System.out);
                    System.out.println("\n请重试或详细描述你的需求。\n");
                }
            }
        } catch (IOException e) {
            System.err.println("❌ 初始化终端失败：" + e.getMessage());
        }
    }

    private void showHelp() {
        System.out.println("\n可用命令:");
        System.out.println("  help       - 显示帮助信息");
        System.out.println("  tools      - 显示可用工具");
        System.out.println("  agents     - 显示可用 Agent");
        System.out.println("  collaborate - 启用多 Agent 协作模式");
        System.out.println("  exit       - 退出聊天");
        System.out.println("\n示例:");
        System.out.println("  - 帮我分析 src/main/java/App.java 的代码结构");
        System.out.println("  - 创建一个计算斐波那契数列的 Java 类");
        System.out.println("  - 审查这个文件的安全问题");
        System.out.println("  - 重构这个模块，提高代码质量");
        System.out.println("  - 查找并修复这个 bug");
        System.out.println("\n高级模式:");
        System.out.println("  - 自动触发: 复杂的任务描述会自动触发多 Agent 协作");
        System.out.println("  - agents: 查看当前可用的所有 Agent 及其能力");
        System.out.println();
    }

    private void showTools(ToolManager toolManager) {
        System.out.println("\n可用工具:");
        System.out.println(toolManager.getToolDescriptions());
        System.out.println();
    }

}
