package com.codingagent.cli.command;

import com.codingagent.agent.decision.AgentRouter;
import com.codingagent.agent.decision.IntentRecognizer;
import com.codingagent.agent.decision.TaskPlanner;
import com.codingagent.agent.decision.IntentRecognizer.Intent;
import com.codingagent.memory.MemoryService;
import com.codingagent.tool.ToolManager;
import picocli.CommandLine;

import java.util.Scanner;

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
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     智能编码助手 - Intelligent Coding Agent    ║");
        System.out.println("║  支持：分析、生成、审查、重构、调试等能力     ║");
        System.out.println("║  新增：自我反思、多 Agent 协作                ║");
        System.out.println("║  输入 'help' 查看帮助，'exit' 退出           ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();

        while (true) {
            System.out.print("👤 你：");
            String input = scanner.nextLine();

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

            if (input.equalsIgnoreCase("collaborate")) {
                System.out.println("🔄 启用多 Agent 协作模式");
                String response = coordinator.coordinate(sessionId, input);
                System.out.println("\n🤖 助手：" + response);
                System.out.println("═══════════════════════════════════════════\n");
                continue;
            }

            try {
                Intent intent = intentRecognizer.recognize(input);
                
                System.out.println("\n🤖 意图识别:");
                System.out.println("   类型：" + intent.getType().getName());
                System.out.println("   置信度：" + intent.getConfidence());
                System.out.println("   理由：" + intent.getReasoning());
                System.out.println();

                if (intent.getConfidence().equals("低")) {
                    System.out.println("⚠️  我不太确定你的意图，能否详细说明？");
                    System.out.println("或者我可以使用智能推理模式来处理这个任务。\n");
                }

                com.codingagent.agent.base.BaseAgent agent = agentRouter.selectAgent(intent);
                
                System.out.println("🤖 已选择 " + agent.getName() + " 来处理你的请求");
                System.out.println();

                String response;
                if (agent instanceof com.codingagent.agent.react.ReActAgent) {
                    System.out.println("🔄 启动智能推理模式 (ReAct)...");
                    response = agent.process(sessionId, input);
                } else {
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

        scanner.close();
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
        System.out.println("  - collaborate: 自动分解任务并协调多个 Agent 完成");
        System.out.println("  - agents: 查看当前可用的所有 Agent 及其能力");
        System.out.println();
    }

    private void showTools(ToolManager toolManager) {
        System.out.println("\n可用工具:");
        System.out.println(toolManager.getToolDescriptions());
        System.out.println();
    }

}
